<template>
  <div>
    <div>
      <input v-model="email" placeholder="Email" />
      <input v-model="username" placeholder="User Name" />
      <input v-model="password" type="password" placeholder="Password" />
      <button @click="login">Login</button>
    </div>

    <div>
      <input v-model="from" type="text" placeholder="Your username" />
      <input v-model="to" type="text" placeholder="Recipient username" />
    </div>
    <br />
    <div>
      <button :disabled="connected" @click="connect">Connect</button>
      <button :disabled="!connected" @click="disconnect">Disconnect</button>
    </div>
    <br />
    <div v-if="connected" id="conversationDiv">
      <input v-model="text" type="text" placeholder="Write a message..." @keyup.enter="sendMessage" />
      <button @click="sendMessage">Send</button>
      <div v-for="(msg, index) in messages" :key="index">
        {{ msg.from }}: {{ msg.text }} ({{ msg.time }})
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import Stomp from "stompjs";

interface MessageOutput {
  from: string;
  text: string;
  time: string;
}

export default defineComponent({
  name: "PrivateChat",
  data() {
    return {
      stompClient: null as Stomp.Client | null,
      connected: false,
      from: "", // this will be the username (from token)
      to: "",
      text: "",
      messages: [] as MessageOutput[],
      email: "",
      password: "",
      username:"",
      jwt: ""
    };
  },
  methods: {
    async login() {
      const response = await fetch("http://localhost:8080/api/auth/authenticate", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          email: this.email,
          password: this.password,
          username: this.username
        })
      });

      if (response.ok) {
        const result = await response.json();
        this.jwt = result.message;
        localStorage.setItem("jwt", this.jwt);
        console.log(this.jwt);
        // extract username from JWT (optional client-side parsing)
        const payload = JSON.parse(atob(this.jwt.split('.')[1]));
        this.from = payload.sub;

        alert("Login successful!");
      } else {
        alert("Login failed");
      }

    },
    // connect() {
    //   const socket = new WebSocket(`ws://localhost:8080/secure?${this.from}`);
    //   this.stompClient = Stomp.over(socket);
    //   this.stompClient.connect(
    //     {},
    //     () => {
    //       this.connected = true;
    //       console.log("Connected");
    //
    //
    //       // Subscribe to private messages for this user
    //       this.stompClient?.subscribe("/user/queue/chat", (messageOutput) => {
    //         const payload = JSON.parse(messageOutput.body) as MessageOutput;
    //         console.log("messageOutput : "+ messageOutput);
    //         console.log("payload : "+ payload);
    //         this.messages.push(payload);
    //       });
    //     },
    //     (error) => {
    //       console.error("Connection error:", error);
    //       this.connected = false;
    //     }
    //   );
    // },
    connect() {
      const token = localStorage.getItem("jwt");
      if (!token) {
        alert("Please log in first.");
        return;
      }

      const socket = new WebSocket(`ws://localhost:8080/secure?token=${token}`);

      this.stompClient = Stomp.over(socket);

      this.stompClient.connect(
        { Authorization: `Bearer ${token}` }, // attach token in headers
        () => {
          this.connected = true;
          console.log("Connected");

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
      if (this.stompClient && this.connected && this.text.trim() && this.to.trim()) {
        this.stompClient.send(
          "/app/secure",
          {},
          JSON.stringify({ from: this.from, to: this.to, text: this.text })
        );
        this.text = "";
      }
    },
  },
});
</script>

<style scoped>
#conversationDiv {
  background-color: #f2f2f2;
}
h2 {
  text-align: center;
  margin-bottom: 10px;
  color: #333;
}

p {
  margin: 0;
  padding: 4px 0;
}

#notifications {
  margin: 15px 0;
  max-height: 200px;
  overflow-y: auto;
  padding: 10px;
  background: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 6px;
}

input[type="text"] {
  width: calc(100% - 22px);
  padding: 10px;
  margin-bottom: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 14px;
}

button {
  background-color: #4caf50;
  color: white;
  padding: 10px 20px;
  margin-top: 5px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.3s ease;
}

button:hover {
  background-color: #45a049;
}

button:disabled {
  background-color: #aaa;
  cursor: not-allowed;
}
</style>
