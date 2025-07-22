<template>
  <div>
    <div>
      <input v-model="from" type="text" placeholder="Choose a nickname" />
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
      <p id="response">
        <template v-for="(msg, index) in messages" :key="index">
          <p style="word-wrap: break-word;">
            {{ msg.from }}: {{ msg.text }} ({{ msg.time }})
          </p>
        </template>
      </p>
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
  name: "HomeView",
  data() {
    return {
      stompClient: null as Stomp.Client | null,
      connected: false,
      from: "",
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
      // Use native WebSocket
      const socket = new WebSocket("ws://localhost:8080/chat");
      this.stompClient = Stomp.over(socket);
      this.stompClient.connect(
        {},
        (frame) => {
          this.setConnected(true);
          console.log("Connected: " + frame);
          this.stompClient?.subscribe("/topic/messages", (messageOutput) => {
            const payload = JSON.parse(messageOutput.body) as MessageOutput;
            this.showMessageOutput(payload);
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
      if (this.stompClient && this.connected) {
        this.stompClient.send(
          "/app/chat",
          {},
          JSON.stringify({ from: this.from, text: this.text })
        );
        this.text = ""; // Clear input after sending
      }
    },
    showMessageOutput(messageOutput: MessageOutput) {
      this.messages.push(messageOutput);
      // Optionally, scroll to bottom or other UI updates here
    },
  },
  mounted() {
    this.disconnect(); // to start with disconnected state
  },
});
</script>

<style scoped>
#conversationDiv {
  background-color: #f2f2f2;
  /* optional styling */
}
</style>
