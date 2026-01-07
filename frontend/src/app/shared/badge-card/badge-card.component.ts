import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
// Am scos LucideAngularModule
import { Badge } from '../../models/badge.model';

@Component({
  selector: 'app-badge-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './badge-card.component.html',
  styleUrls: ['./badge-card.component.css']
})
export class BadgeCardComponent {
  @Input() badge!: Badge;
  @Input() isUnlocked: boolean = false;

  get tooltip(): string {
    if (!this.badge) return '';
    const criteriaText = this.badge.criteria ? ` (${this.badge.criteria})` : '';
    return `${this.badge.description}${criteriaText}`;
  }

}
