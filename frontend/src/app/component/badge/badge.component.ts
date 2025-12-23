import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideAngularModule, Trophy, Star, Medal, Crown } from 'lucide-angular';

@Component({
  selector: 'app-badge',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  templateUrl: './badge.component.html',
  styleUrls: ['./badge.component.css']
})
export class BadgeComponent {
  @Input() name: string = '';
  @Input() description: string = '';
  @Input() criteria: string = '';

  readonly icons = { Trophy, Star, Medal, Crown };

  // Logica de mapare nume -> iconiță (pentru că lipsește iconName din Record)
  getBadgeIcon(): any {
    const n = this.name.toLowerCase();
    if (n.includes('step')) return this.icons.Trophy;
    if (n.includes('xp') || n.includes('point')) return this.icons.Crown;
    if (n.includes('five')) return this.icons.Star;
    return this.icons.Medal;
  }
}
