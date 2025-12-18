import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import {AcceptChallengeModalComponent} from './accept-challenge-modal'; // Important pentru ngModel

describe('AcceptChallengeModalComponent', () => {
  let component: AcceptChallengeModalComponent;
  let fixture: ComponentFixture<AcceptChallengeModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AcceptChallengeModalComponent ],
      imports: [ FormsModule ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AcceptChallengeModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
