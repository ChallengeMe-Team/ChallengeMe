/**
 * UNIT TEST SPECIFICATIONS: Ownership and Permission Logic
 * -----------------------------------------------------------------------------------
 * Purpose: To verify that the UI correctly enforces ownership rules,
 * ensuring users can only manage resources they created.
 */

import { TestBed } from '@angular/core/testing';
import { ChallengesComponent } from './challenges-component';
import { AuthService } from '../../../services/auth.service';

describe('ChallengesComponent - Ownership Logic', () => {
  let component: ChallengesComponent;
  let authService: AuthService;

  beforeEach(() => {
    // MOCK: Simulates an authenticated user session
    authService = {
      currentUser: () => ({ username: 'stefan' })
    } as any;

    TestBed.configureTestingModule({
      imports: [ChallengesComponent],
      providers: [
        { provide: AuthService, useValue: authService }
      ]
    });

    component = TestBed.createComponent(ChallengesComponent).componentInstance;
  });

  /** TEST CASE 1: Verifies that the edit modal opens for the owner */
  it('should allow editing challenge if user is owner', () => {
    const challenge = { createdBy: 'stefan' } as any;

    // Simulate edit request
    component.handleEdit(challenge);

    expect(component.isEditModalOpen()).toBeTrue();
  });

  /** TEST CASE 2: Verifies that unauthorized edit requests are blocked and reported */
  it('should block editing challenge if user is NOT owner', () => {
    spyOn(component, 'showToast');

    const challenge = { createdBy: 'altUser' } as any;

    // Simulate unauthorized edit request
    component.handleEdit(challenge);

    expect(component.showToast).toHaveBeenCalledWith(
      "You can only edit challenges created by you.",
      "error"
    );
    expect(component.isEditModalOpen()).toBeFalse();
  });
});
