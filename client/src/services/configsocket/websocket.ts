// services/websocketService.ts

import { Client, StompSubscription, IMessage, Frame } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { localStorageAction } from "@/utils/storage";
import { ACCESS_TOKEN_STORAGE_KEY } from "@/constants/storagekey";

// ENV
export const { VITE_BASE_URL_CLIENT_SOCKET } = import.meta.env || {};

type SubscriptionItem = {
  stompSub: StompSubscription;
  callbacks: Set<(msg: any) => void>;
};

class WebSocketService {
  private client: Client | null = null;
  private connected = false;
  private subscriptions: Record<string, SubscriptionItem> = {};

  private isConnecting = false;
  private waitingCallbacks: (() => void)[] = [];
  private pendingSubscriptions: Record<string, Set<(msg: any) => void>> = {};

  connect(callback?: () => void) {
    if (this.connected) {
      console.log("✅ WebSocket đã kết nối.");
      if (callback) callback();
      return;
    }

    if (this.isConnecting) {
      if (callback) this.waitingCallbacks.push(callback);
      return;
    }

    this.isConnecting = true;

    const token = localStorageAction.get(ACCESS_TOKEN_STORAGE_KEY);
    // if (!token) {
    //   console.error("🚨 Không tìm thấy token!");
    //   this.isConnecting = false;
    //   return;
    // }

    const socketURL = `${VITE_BASE_URL_CLIENT_SOCKET}/ws?token=${encodeURIComponent(token)}`;
    const socket = new SockJS(socketURL);

    this.client = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,

      onConnect: (frame: Frame) => {
        console.log("✅ WebSocket đã kết nối:", frame);
        this.connected = true;
        this.isConnecting = false;

        if (callback) callback();
        this.waitingCallbacks.forEach((cb) => cb());
        this.waitingCallbacks = [];

        // Đăng ký lại các pendingSubscriptions
        Object.entries(this.pendingSubscriptions).forEach(([destination, callbacks]) => {
          callbacks.forEach((cb) => this.subscribe(destination, cb));
        });
        this.pendingSubscriptions = {};
      },

      onStompError: (frame) => {
        console.error("❌ STOMP Error:", frame);
      },

      onWebSocketClose: () => {
        console.warn("⚠️ WebSocket đóng kết nối.");
        this.connected = false;
        this.isConnecting = false;
      },
    });

    this.client.activate();
  }

  /** Đăng ký nhận message từ 1 topic */
  subscribe(destination: string, callback: (msg: any) => void) {
    if (!this.client || !this.connected) {
      console.warn("⏳ WebSocket chưa kết nối, delay subscribe...");
      if (!this.pendingSubscriptions[destination]) {
        this.pendingSubscriptions[destination] = new Set();
      }
      this.pendingSubscriptions[destination].add(callback);
      this.connect(() => this.subscribe(destination, callback));
      return;
    }

    if (!this.subscriptions[destination]) {
      const stompSub = this.client.subscribe(destination, (message: IMessage) => {
        let parsed;
        try {
          parsed = JSON.parse(message.body);
        } catch {
          parsed = message.body;
        }

        this.subscriptions[destination]?.callbacks.forEach((cb) => cb(parsed));
      });

      this.subscriptions[destination] = {
        stompSub,
        callbacks: new Set([callback]),
      };
    } else {
      this.subscriptions[destination].callbacks.add(callback); // ✅ Không sợ trùng vì là Set 
    }
  }

  /** Hủy đăng ký 1 topic */
  unsubscribe(destination: string) {
    const sub = this.subscriptions[destination];
    if (sub) {
      sub.stompSub.unsubscribe();
      delete this.subscriptions[destination];
      console.log(`🔕 Unsubscribed: ${destination}`);
    }
  }

  /** Gửi tin nhắn */
  sendMessage(destination: string, message: any) {
    if (!this.client || !this.connected) {
      console.warn("🚨 WebSocket chưa kết nối. Delay gửi message...");
      this.connect(() => this.sendMessage(destination, message));
      return;
    }

    this.client.publish({
      destination,
      body: JSON.stringify(message),
    });
  }

  /** Ngắt kết nối hoàn toàn */
  disconnect() {
    if (this.client) {
      Object.keys(this.subscriptions).forEach((destination) => this.unsubscribe(destination));
      this.client.deactivate();
      this.connected = false;
      this.isConnecting = false;
      console.log("🔌 Đã ngắt kết nối WebSocket.");
    }
  }

  /** Kiểm tra trạng thái kết nối */
  isConnected() {
    return this.connected;
  }
}

export const websocketService = new WebSocketService();
