<template>
  <div>
    <h2>Private Chat</h2>
    <p><strong>Logged in as:</strong> {{ user }}</p>

    <div id="notifications">
      <p v-for="(note, index) in notifications" :key="index">
        {{ note.from }}: {{ note.message }}
      </p>
    </div>

    <input v-model="message" type="text" placeholder="Enter your message" @keyup.enter="sendPrivateMessage" />
    <button @click="sendPrivateMessage">Send</button>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import Stomp from "stompjs";

interface Notification {
  from: string;
  message: string;
}

export default defineComponent({
  name: "ChatView",
  data() {
    return {
      stompClient: null as Stomp.Client | null,
      connected: false,
      user: "",
      message: "",
      notifications: [] as Notification[],
    };
  },
  methods: {
    setConnected(connected: boolean) {
      this.connected = connected;
    },
    connect() {
      const socket = new WebSocket(`ws://localhost:8080/websocket?${this.user}`);
      this.stompClient = Stomp.over(socket);
      this.stompClient.connect(
        {},
        (frame) => {
          this.setConnected(true);
          console.log("Connected: " + frame);

          this.stompClient?.subscribe("/user/queue/messages", (msg) => {
            const payload = JSON.parse(msg.body) as Notification;
            this.notifications.push(payload);
          });
        },
        (error) => {
          console.error("Connection error:", error);
          this.setConnected(false);
        }
      );
    },
    sendPrivateMessage() {
      if (!this.stompClient || !this.connected) return;

      const to = this.user === "admin" ? "user" : "admin"; // simple logic for demo
      const payload = JSON.stringify({
        from: this.user,
        to: to,
        message: this.message,
      });

      this.stompClient.send("/app/private-message", {}, payload);
      this.message = "";
    },
  },
  mounted() {
    this.user = prompt("Enter your username (e.g., admin or user):") || "";
    this.connect();
  },
});
</script>
<style scoped>
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
