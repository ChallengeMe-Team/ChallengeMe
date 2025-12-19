import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideAngularModule, AlertTriangle, Info, X } from 'lucide-angular';

@Component({
  selector: 'app-confirmation-modal',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  templateUrl: './confirmation-modal.component.html',
  styles: []
})
export class ConfirmationModalComponent {
  @Input() isVisible = false;
  @Input() title = 'Are you sure?';
  @Input() message = '';
  @Input() confirmText = 'Confirm';
  @Input() cancelText = 'Cancel';
  @Input() type: 'danger' | 'info' | 'warning' = 'info'; // Schimbă culoarea butonului

  @Output() close = new EventEmitter<void>();
  @Output() confirm = new EventEmitter<void>();

  readonly icons = { AlertTriangle, Info, X };

  onCancel() {
    this.close.emit();
  }

  onConfirm() {
    this.confirm.emit();
  }
}
