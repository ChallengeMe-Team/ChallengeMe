import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideAngularModule, Edit, Trash2, PlusCircle } from 'lucide-angular';

import { ChallengeService } from '../../../services/challenge.service';
import { AuthService } from '../../../services/auth.service';
import { Challenge } from '../challenges/challenge.model';
import { ChallengeFormComponent } from '../../forms/challenge-form/challenge-form';
import { ToastComponent } from '../../../shared/toast/toast-component';

@Component({
  selector: 'app-my-challenges',
  standalone: true,
  imports: [CommonModule, ChallengeFormComponent, ToastComponent, LucideAngularModule],
  // Nu avem CSS dedicat, refolosim stilul de la challenges sau global
  templateUrl: './my-challenges-component.html',
  styles: []
})
export class MyChallengesComponent implements OnInit {
  public challengeService = inject(ChallengeService);
  private auth = inject(AuthService);

  // State
  myChallenges = signal<Challenge[]>([]);
  isLoading = signal(false);

  // Modale
  isEditModalOpen = signal(false);
  editChallengeData = signal<any | null>(null);

  isDeleteModalOpen = signal(false);
  challengeToDelete = signal<Challenge | null>(null);

  // Toast
  toastMessage = signal<string | null>(null);
  toastType = signal<'success' | 'error'>('success');

  // Icons
  readonly icons = { Edit, Trash2, PlusCircle };

  ngOnInit() {
    this.loadMyChallenges();
  }

  loadMyChallenges() {
    // 1. Luăm userul curent din Auth Service (Frontend decision)
    const currentUser = this.auth.currentUser();

    if (!currentUser || !currentUser.username) {
      console.error("No user logged in!");
      return;
    }

    this.isLoading.set(true);

    // 2. Trimitem username-ul explicit către Backend
    this.challengeService.getUserChallenges(currentUser.username).subscribe({
      next: (data) => {
        this.myChallenges.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Error fetching user challenges:', err);
        this.isLoading.set(false);
      }
    });
  }

  // --- ACTIUNI ---

  // 1. EDIT
  openEditModal(challenge: Challenge) {
    this.editChallengeData.set(challenge);
    this.isEditModalOpen.set(true);
  }

  onChallengeUpdated(updatedData: any) {
    this.challengeService.updateChallenge(updatedData.id, updatedData).subscribe({
      next: () => {
        this.showToast('Challenge updated!', 'success');
        this.isEditModalOpen.set(false);
        this.loadMyChallenges();
      },
      error: (err) => this.showToast('Update failed.', 'error')
    });
  }

  // 2. DELETE
  confirmDelete(challenge: Challenge) {
    this.challengeToDelete.set(challenge);
    this.isDeleteModalOpen.set(true);
  }

  performDelete() {
    const target = this.challengeToDelete();
    if (!target) return;

    this.challengeService.deleteChallenge(target.id).subscribe({
      next: () => {
        this.showToast('Challenge deleted.', 'success');
        this.isDeleteModalOpen.set(false);
        this.myChallenges.update(list => list.filter(c => c.id !== target.id));
      },
      error: (err) => {
        this.showToast('Delete failed.', 'error');
        this.isDeleteModalOpen.set(false);
      }
    });
  }

  showToast(msg: string, type: 'success' | 'error') {
    this.toastMessage.set(msg);
    this.toastType.set(type);
    setTimeout(() => this.toastMessage.set(null), 3000);
  }
}
