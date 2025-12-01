import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-challenge-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './challenge-form.html',
  styleUrls: ['./challenge-form.css']
})
export class ChallengeFormComponent {

  @Input() mode: 'create' | 'edit' = 'create';
  @Input() challengeData: any = null;

  @Output() cancel = new EventEmitter<void>();
  @Output() submitChallenge = new EventEmitter<any>();

  challenge = {
    id: null,
    title: '',
    description: '',
    category: '',
    difficulty: '',
    points: 100
  };

  difficulties = ['EASY', 'MEDIUM', 'HARD'];

  ngOnInit() {
    if (this.mode === 'edit' && this.challengeData) {
      this.challenge = { ...this.challengeData };
    }
  }

  onSubmit() {
    if (!this.challenge.title || !this.challenge.description || !this.challenge.category || !this.challenge.difficulty) {
      alert('Please fill out all required fields!');
      return;
    }

    this.submitChallenge.emit(this.challenge);
  }
}
