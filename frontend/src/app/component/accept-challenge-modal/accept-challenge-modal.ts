import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

/**
 * Component representing a high-fidelity modal for challenge acceptance.
 * This component acts as a "Soulbound Contract" where users must define
 * their commitment by selecting a start date and a target deadline.
 * * Key Features:
 * - Reactive date validation.
 * - Real-time commitment calculation (duration in days).
 * - Prevention of past-date selection.
 */
@Component({
  selector: 'app-accept-challenge-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './accept-challenge-modal.html',
  styleUrls: ['./accept-challenge-modal.css']
})
export class AcceptChallengeModalComponent{
  /** The title of the challenge being accepted, passed from the parent component. */
  @Input() challengeTitle: string | undefined = '';
  /** Controls the visibility of the modal overlay. */
  @Input() isVisible: boolean = false;
  /** Emitted when the user closes the modal without signing. */
  @Output() close = new EventEmitter<void>();
  /** Emitted when the contract is valid and signed, returning the chosen dates. */
  @Output() confirm = new EventEmitter<{ start: string, end: string }>();

  /** Initialized to current system date (ISO format). */
  startDate: string = new Date().toISOString().split('T')[0];
  /** User-defined deadline for challenge completion. */
  targetDeadline: string = '';
  /** Dynamic message showing the calculated duration of the commitment. */
  commitmentMessage: string = '';
  /** Feedback message for validation errors (e.g., deadline before start date). */
  errorMsg: string = '';

  /**
   * Orchestrates the validation logic whenever a date input changes.
   * Checks for:
   * 1. Start date not in the past.
   * 2. Mandatory target deadline.
   * 3. Logical sequence (Deadline > Start).
   */
  onDateChange(): void {
    this.errorMsg = '';
    this.commitmentMessage = '';

    if (!this.startDate) return;

    const todayStr = new Date().toISOString().split('T')[0];
    if (this.startDate < todayStr) {
      this.errorMsg = "Start date cannot be in the past.";
      return;
    }

    if (!this.targetDeadline) {
      this.errorMsg = "A target deadline is required to sign the contract.";
      return;
    }

    const start = new Date(this.startDate);
    const end = new Date(this.targetDeadline);

    if (end <= start) {
      this.errorMsg = "Deadline must be after the start date.";
      return;
    }

    const diffTime = Math.abs(end.getTime() - start.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    this.commitmentMessage = `You are committing to finish this in ${diffDays} days.`;
  }

  /** Finalizes the contract and emits data to the parent service. */
  onConfirm(): void {
    if (!this.errorMsg && this.startDate && this.targetDeadline) {
      this.confirm.emit({ start: this.startDate, end: this.targetDeadline });
    }
  }

  /** Resets state and notifies parent to close the UI. */
  onCancel(): void {
    this.close.emit();
    this.startDate = new Date().toISOString().split('T')[0];
    this.targetDeadline = '';
    this.errorMsg = '';
    this.commitmentMessage = '';
  }
}
