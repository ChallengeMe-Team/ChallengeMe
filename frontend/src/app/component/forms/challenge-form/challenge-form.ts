import { HttpClient } from '@angular/common/http'; // Import nou
import { Component, EventEmitter, Input, Output, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-challenge-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './challenge-form.html',
  styleUrls: ['./challenge-form.css']
})
export class ChallengeFormComponent implements OnInit{
  private http = inject(HttpClient); // Injectăm HttpClient

  forbiddenWords: string[] = []; // Listă care va fi populată din fișier

  ngOnInit() {
    this.loadForbiddenWords(); // Încărcăm cuvintele la pornire

    if (this.mode === 'edit' && this.challengeData) {
      this.challenge = { ...this.challengeData };
    }
  }

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

  errors = {
    title: '',
    description: ''
  };

  difficulties = ['EASY', 'MEDIUM', 'HARD'];

  onSubmit() {
    // Validăm ambele câmpuri încă o dată înainte de submit
    this.validateField('title');
    this.validateField('description');

    if (this.errors.title || this.errors.description) {
      return; // Nu trimite dacă există cuvinte interzise
    }

    if (!this.challenge.title || !this.challenge.description || !this.challenge.category || !this.challenge.difficulty) {
      alert('Please fill out all required fields!');
      return;
    }

    this.submitChallenge.emit(this.challenge);
  }

  private loadForbiddenWords() {
    this.http.get('assets/bad-words.txt', { responseType: 'text' }).subscribe({
      next: (data) => {
        this.forbiddenWords = data.split(/\r?\n/)
          .map(word => word.trim().toLowerCase())
          .filter(word => word.length > 0);

        console.log('Cuvinte încărcate:', this.forbiddenWords.length);
      }
    });
  }

  validateField(field: 'title' | 'description') {
    const value = this.challenge[field];
    if (!value || this.forbiddenWords.length === 0) {
      this.errors[field] = '';
      return;
    }

    const lowerInput = value.toLowerCase();

    // Verificare directă și simplă
    const hasBadContent = this.forbiddenWords.some(word => lowerInput.includes(word));

    this.errors[field] = hasBadContent ? 'Inappropriate content detected!' : '';
  }

  get isFormInvalid(): boolean {
    // Butonul va fi dezactivat dacă există erori de conținut SAU câmpuri obligatorii goale
    return !!this.errors.title ||
      !!this.errors.description ||
      !this.challenge.title.trim() ||
      !this.challenge.category.trim() ||
      !this.challenge.difficulty;
  }
}
