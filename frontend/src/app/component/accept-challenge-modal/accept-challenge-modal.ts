import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-accept-challenge-modal',
  standalone: true,  // <--- ACEASTA LINIE ESTE CHEIA
  imports: [CommonModule, FormsModule], // <--- AICI IMPORTĂM DEPENDENȚELE LUI
  templateUrl: './accept-challenge-modal.html',
  styleUrls: ['./accept-challenge-modal.css']
})
export class AcceptChallengeModalComponent {
  // ... restul codului tău (logica pe care ai scris-o deja) rămâne neschimbat
  @Input() challengeTitle: string | undefined = '';
  @Input() isVisible: boolean = false;
  @Output() close = new EventEmitter<void>();
  @Output() confirm = new EventEmitter<{ start: string, end: string }>();

  startDate: string = new Date().toISOString().split('T')[0];
  targetDeadline: string = '';
  commitmentMessage: string = '';
  errorMsg: string = '';

  onDateChange(): void {
    // ... (codul tău de validare) ...
    this.errorMsg = '';
    this.commitmentMessage = '';
    if (!this.startDate) return;
    const start = new Date(this.startDate);
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    if (this.startDate < new Date().toISOString().split('T')[0]) {
      this.errorMsg = "Start date cannot be in the past.";
      return;
    }

    if (this.targetDeadline) {
      const end = new Date(this.targetDeadline);
      if (end <= start) {
        this.errorMsg = "Deadline must be after the start date.";
        return;
      }
      const diffTime = Math.abs(end.getTime() - start.getTime());
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      this.commitmentMessage = `You are committing to finish this in ${diffDays} days.`;
    }
  }

  onConfirm(): void {
    if (!this.errorMsg && this.startDate) {
      this.confirm.emit({ start: this.startDate, end: this.targetDeadline });
    }
  }

  onCancel(): void {
    this.close.emit();
    this.startDate = new Date().toISOString().split('T')[0];
    this.targetDeadline = '';
    this.errorMsg = '';
    this.commitmentMessage = '';
  }
}
