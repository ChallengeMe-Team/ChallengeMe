import { TestBed } from '@angular/core/testing';
import { ChallengesComponent } from './challenges-component';
import { AuthService } from '../../../services/auth.service';

describe('ChallengesComponent - Ownership Logic', () => {
  let component: ChallengesComponent;
  let authService: AuthService;

  beforeEach(() => {
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

  it('should allow editing challenge if user is owner', () => {
    const challenge = { createdBy: 'stefan' } as any;

    component.onChallengeDoubleClick(challenge);

    expect(component.isEditModalOpen()).toBeTrue();
  });

  it('should block editing challenge if user is NOT owner', () => {
    spyOn(component, 'showToast');

    const challenge = { createdBy: 'altUser' } as any;

    component.onChallengeDoubleClick(challenge);

    expect(component.showToast).toHaveBeenCalledWith(
      "You can only edit challenges created by you.",
      "error"
    );
    expect(component.isEditModalOpen()).toBeFalse();
  });
});
