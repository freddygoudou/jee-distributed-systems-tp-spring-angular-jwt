import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ChatService } from '../../services/chat.service';

interface ChatMessage {
  from: 'user' | 'bot';
  text: string;
}

@Component({
  selector: 'app-chat-widget',
  imports: [FormsModule],
  templateUrl: './chat-widget.html',
  styleUrl: './chat-widget.css',
})
export class ChatWidget {
  private chatService = inject(ChatService);

  open = signal(false);
  messages = signal<ChatMessage[]>([
    { from: 'bot', text: 'Bonjour 👋 Je suis l\'assistant Digital Banking. Posez-moi une question sur les comptes, opérations, etc.' },
  ]);
  input = '';
  loading = signal(false);

  toggle(): void {
    this.open.update((v) => !v);
  }

  send(): void {
    const text = this.input.trim();
    if (!text || this.loading()) return;
    this.messages.update((m) => [...m, { from: 'user', text }]);
    this.input = '';
    this.loading.set(true);
    this.chatService.ask(text).subscribe({
      next: (res) => {
        this.messages.update((m) => [...m, { from: 'bot', text: res.answer }]);
        this.loading.set(false);
      },
      error: () => {
        this.messages.update((m) => [...m, { from: 'bot', text: 'Erreur de connexion au service de chat.' }]);
        this.loading.set(false);
      },
    });
  }
}
