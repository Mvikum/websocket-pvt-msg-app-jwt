<template>
  <div>
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
    <div v-show="connected" id="conversationDiv">
      <input v-model="text" type="text" placeholder="Write a message..." />
      <button @click="sendMessage">Send</button>
      <div id="response">
        <div v-for="(msg, index) in messages" :key="index" style="word-wrap: break-word;">
          {{ msg.from }}: {{ msg.text }} ({{ msg.time }})
        </div>
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
      from: "",
      to: "",
      text: "",
      messages: [] as MessageOutput[],
    };
  },
  methods: {
    setConnected(connected: boolean) {
      this.connected = connected;
      if (!connected) {
        this.messages = [];
      }
    },
    connect() {
      const socket = new WebSocket("ws://localhost:8080/secure");
      this.stompClient = Stomp.over(socket);
      this.stompClient.connect(
        {},
        (frame) => {
          this.setConnected(true);
          console.log("Connected: " + frame);

          // Subscribe to the private queue for the logged-in user
          this.stompClient?.subscribe("/user/hidden/chat", (messageOutput) => {
            const payload = JSON.parse(messageOutput.body) as MessageOutput;
            this.messages.push(payload);
          });
        },
        (error) => {
          console.error("Connection error:", error);
          this.setConnected(false);
        }
      );
    },
    disconnect() {
      if (this.stompClient) {
        this.stompClient.disconnect(() => {
          console.log("Disconnected");
          this.setConnected(false);
          this.stompClient = null;
        });
      } else {
        this.setConnected(false);
      }
    },
    sendMessage() {
      if (this.stompClient && this.connected && this.text.trim() && this.to.trim()) {
        this.stompClient.send(
          "/app/secure", // matches @MessageMapping("/secure")
          {},
          JSON.stringify({ from: this.from, to: this.to, text: this.text })
        );
        this.text = ""; // Clear input after sending
      }
    },
  },
  mounted() {
    this.disconnect();
  },
});
</script>

<style scoped>
#conversationDiv {
  background-color: #f2f2f2;
}
</style>
