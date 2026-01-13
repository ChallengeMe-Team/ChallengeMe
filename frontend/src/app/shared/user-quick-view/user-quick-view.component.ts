import { Component, Input, Output, EventEmitter, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserProfile } from '../../models/user.model';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-user-quick-view',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  templateUrl: './user-quick-view.component.html',
  styleUrls: ['./user-quick-view.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class UserQuickViewComponent {
  @Input({ required: true }) isOpen = false;
  @Input() isLoading = false;
  @Input() profile: UserProfile | null = null;

  @Output() close = new EventEmitter<void>();

  closeModal() {
    this.close.emit();
  }
}
