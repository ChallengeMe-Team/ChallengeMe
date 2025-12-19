import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideAngularModule, X, UploadCloud, CheckCircle, Image as ImageIcon } from 'lucide-angular';

@Component({
  selector: 'app-complete-challenge-modal',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  templateUrl: './complete-challenge-modal-component.html',
  styles: [] // Styles are handled via Tailwind classes in the template
})
export class CompleteChallengeModalComponent {
  @Input() isVisible = false;
  @Input() challengeTitle = '';
  @Input() xpReward = 0;

  @Output() close = new EventEmitter<void>();
  @Output() confirmCompletion = new EventEmitter<void>(); // Emits when user clicks "Claim Reward"

  previewUrl: string | null = null;
  fileName: string | null = null;
  isDragging = false;

  // Icons for the template
  readonly icons = { X, UploadCloud, CheckCircle, ImageIcon };

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.processFile(input.files[0]);
    }
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
    this.isDragging = true;
  }

  onDragLeave(event: DragEvent) {
    event.preventDefault();
    this.isDragging = false;
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    this.isDragging = false;
    if (event.dataTransfer && event.dataTransfer.files.length > 0) {
      this.processFile(event.dataTransfer.files[0]);
    }
  }

  processFile(file: File) {
    // Basic validation: ensure it's an image
    if (!file.type.startsWith('image/')) {
      alert('Please select an image file.');
      return;
    }

    this.fileName = file.name;

    // Create a local preview URL
    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.previewUrl = e.target.result;
    };
    reader.readAsDataURL(file);
  }

  removeFile() {
    this.previewUrl = null;
    this.fileName = null;
  }

  onClaim() {
    if (this.previewUrl) {
      this.confirmCompletion.emit();
      this.resetForm(); // Optional: reset state after success
    }
  }

  onCancel() {
    this.close.emit();
    this.resetForm();
  }

  private resetForm() {
    this.previewUrl = null;
    this.fileName = null;
    this.isDragging = false;
  }
}
