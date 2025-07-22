<script lang="ts">
import { defineComponent } from 'vue'
import Stomp from "stompjs";

interface MessageOutput {
  from: string;
  text: string;
  time: string;
}
export default defineComponent({
  name: 'MessageView',
  data() {
    return {
      stompClient: null as Stomp.Client | null,
      connected: false,
      from: "",
      to: "",
      text: "",
      messages: [] as MessageOutput[],
      email: "",
      password: "",
      jwt: "",
      loginDialog: true,
      isAuthenticated: false
    };
  },
  methods: {
    async login() {
      try {
        const response = await fetch("http://localhost:8080/api/auth/authenticate", {
          method: "POST",
          headers: {
            "Content-Type": "application/json"
          },
          body: JSON.stringify({
            email: this.email,
            password: this.password
          })
        });

        if (response.ok) {
          const result = await response.json();
          this.jwt = result.message;
          localStorage.setItem("jwt", this.jwt);

          const payload = JSON.parse(atob(this.jwt.split('.')[1]));
          this.from = payload.sub;

          this.isAuthenticated = true;
          this.loginDialog = false;

          console.log("Login successful");
        } else {
          alert("Login failed. Please check your credentials.");
        }
      } catch (e) {
        console.error("Login error:", e);
      }
    },
    connect() {
      const token = localStorage.getItem("jwt");
      if (!token) {
        alert("Please log in first.");
        return;
      }
      // const socket = new WebSocket(`ws://localhost:8080/secure?token=${token}`);

      const socket = new WebSocket(`ws://localhost:8080/secure?token=${token}`);
      // const socket = new WebSocket(`ws://localhost:8080/secure`);
      // console.log(socket);

      this.stompClient = Stomp.over(socket);

      this.stompClient.connect(
        { Authorization: `Bearer ${token}` },
        // {},
        () => {
          this.connected = true;
          console.log("Connected");
          console.log(token);

          this.stompClient?.subscribe("/user/queue/chat", (messageOutput) => {
            const payload = JSON.parse(messageOutput.body) as MessageOutput;
            this.messages.push(payload);
          });
        },
        (error) => {
          console.error("Connection error:", error);
          this.connected = false;
        }
      );
    },
    disconnect() {
      if (this.stompClient) {
        this.stompClient.disconnect(() => {
          console.log("Disconnected");
          this.connected = false;
          this.stompClient = null;
        });
      }
    },
    sendMessage() {
      const token = localStorage.getItem("jwt");
      if (this.stompClient && this.connected && this.text.trim() && this.to.trim()) {
        this.stompClient.send(
          "/app/secure",
          { Authorization: `Bearer ${token}` },
          JSON.stringify({ from: this.from, to: this.to, text: this.text })
        );
        this.text = "";
      }
    },
  },
});
</script>

<template>
  <v-container>
    <!-- Dialog box -->
    <v-dialog v-model="loginDialog" persistent max-width="400">
      <v-card>
        <v-card-title>Login</v-card-title>
        <v-card-text>
          <v-text-field v-model="email" label="Email" type="email" required />
          <v-text-field v-model="password" label="Password" type="password" required />
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" @click="login">Login</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- chat -->
    <v-row v-if="isAuthenticated">
      <v-col cols="12" sm="6">
        <v-row><h3>Logged as : {{from}}</h3></v-row>

        <v-text-field v-model="to" label="Recipient username" />
      </v-col>

      <v-col cols="12">
        <v-btn :disabled="connected" color="success" @click="connect">Connect</v-btn>
        <v-btn :disabled="!connected" color="error" @click="disconnect">Disconnect</v-btn>
      </v-col>

      <v-col cols="12" v-if="connected">
        <v-text-field
          v-model="text"
          label="Write a message..."
          @keyup.enter="sendMessage"
        />
        <v-btn @click="sendMessage">Send</v-btn>
      </v-col>

      <v-col cols="12">
        <v-list two-line>
          <v-list-item v-for="(msg, index) in messages" :key="index">
            <v-list-item-content>
              <v-list-item-title>{{ msg.from }}: {{ msg.text }}</v-list-item-title>
              <v-list-item-subtitle>{{ msg.time }}</v-list-item-subtitle>
            </v-list-item-content>
          </v-list-item>
        </v-list>
      </v-col>
    </v-row>
  </v-container>
</template>


<style scoped>

</style>
