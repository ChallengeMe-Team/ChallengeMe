import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideAngularModule, X, UploadCloud, CheckCircle, Image as ImageIcon } from 'lucide-angular';

/**
 * Component responsible for the final stage of a challenge: claiming rewards.
 * It requires the user to provide visual proof (image upload) before finalizing.
 * * Key Features:
 * - Drag & Drop Image Support: Seamless file handling using native browser events.
 * - Local Preview: Utilizes FileReader API to display the selected image without server-side storage.
 * - Dynamic UI State: Manages visual feedback for dragging states and reward details.
 */
@Component({
  selector: 'app-complete-challenge-modal',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  templateUrl: './complete-challenge-modal-component.html',
  styles: [] // Styles are handled via Tailwind classes in the template
})
export class CompleteChallengeModalComponent {
  /** Visibility toggle passed from the parent component. */
  @Input() isVisible = false;

  /** Title of the challenge being finalized. */
  @Input() challengeTitle = '';

  /** XP amount to be added to the user's total upon confirmation. */
  @Input() xpReward = 0;

  /** Triggered when the user exits the modal without claiming. */
  @Output() close = new EventEmitter<void>();

  /** Triggered upon successful proof selection and 'Claim' click. */
  @Output() confirmCompletion = new EventEmitter<void>(); // Emits when user clicks "Claim Reward"

  /** Local Base64 string for image preview. */
  previewUrl: string | null = null;
  /** Display name of the uploaded file. */
  fileName: string | null = null;
  /** State flag for UI drag-and-drop feedback. */
  isDragging = false;

  // Icons for the template
  readonly icons = { X, UploadCloud, CheckCircle, ImageIcon };

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.processFile(input.files[0]);
    }
  }

  /**
   * Standard file input handler.
   * Extracts the first file and initiates processing.
   */
  onDragOver(event: DragEvent) {
    event.preventDefault();
    this.isDragging = true;
  }

  onDragLeave(event: DragEvent) {
    event.preventDefault();
    this.isDragging = false;
  }

  /** * Implements Drag & Drop logic.
   * Prevents default browser behavior to allow custom drop zones.
   */
  onDrop(event: DragEvent) {
    event.preventDefault();
    this.isDragging = false;
    if (event.dataTransfer && event.dataTransfer.files.length > 0) {
      this.processFile(event.dataTransfer.files[0]);
    }
  }

  /**
   * Validates file type and generates a local data URL for the UI.
   * @param file The image file from input or drop event.
   */
  processFile(file: File) {
    if (!file.type.startsWith('image/')) {
      alert('Please select an image file.');
      return;
    }

    this.fileName = file.name;

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

  /** Emits confirmation and resets the internal state. */
  onClaim() {
    if (this.previewUrl) {
      this.confirmCompletion.emit();
      this.resetForm();
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
