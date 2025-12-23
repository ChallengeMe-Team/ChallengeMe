import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideAngularModule } from 'lucide-angular';
import { Badge } from '../../models/badge.model';

@Component({
  selector: 'app-badge-card',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  templateUrl: './badge-card.component.html',
  styleUrls: ['./badge-card.component.css']
})
export class BadgeCardComponent {
  // Input conform Acceptance Criteria
  @Input() badge!: Badge;
  @Input() isUnlocked: boolean = false;

  get tooltip(): string {
    return `${this.badge.description} (${this.badge.criteria})`;
  }
}
