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
    // MODIFICAT: Am scos 'criteria' care cauza eroarea
    return this.badge.description;
  }

  handleMissingImage(event: Event) {
    const imgElement = event.target as HTMLImageElement;
    // 1. Oprim bucla infinită (anulăm handler-ul de eroare)
    imgElement.onerror = null;
    // 2. Setăm calea ABSOLUTĂ către imaginea default
    imgElement.src = '/assets/badges/default.png';
  }
}
