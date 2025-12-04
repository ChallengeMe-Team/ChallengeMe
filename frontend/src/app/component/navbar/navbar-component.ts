import {
    Component,
    ChangeDetectionStrategy,
    inject,
    ElementRef,
    HostListener,
    EventEmitter,
    Output
} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule, Router} from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {ChallengeService} from '../../services/challenge.service';
import {
    LucideAngularModule,
    User,
    FileText,
    Users,
    Settings,
    LogOut,
    ChevronDown,
    PlusCircle,
    Menu
} from 'lucide-angular';

@Component({
    selector: 'app-navbar',
    standalone: true,
    imports: [CommonModule, LucideAngularModule, RouterModule],
    templateUrl: './navbar-component.html',
    styleUrls: ['./navbar-component.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class NavbarComponent {

    private authService = inject(AuthService);
    private challengeService = inject(ChallengeService);
    private elementRef = inject(ElementRef);
    private router = inject(Router);

    @Output() createChallengeRequest = new EventEmitter<void>();
    @Output() toastRequest = new EventEmitter<{ message: string, type: 'success' | 'error' }>();

    // Icons
    readonly icons = {User, FileText, Users, Settings, LogOut, ChevronDown, PlusCircle, Menu};

    isDropdownOpen = false;
    isMenuOpen = false;

    // User data
    user = this.authService.currentUser;

    // Helpers
    get username(): string {
        return this.user()?.username || 'Guest';
    }

    get userPoints(): number {
        return this.user()?.points || 0;
    }

    get userLevel(): number {
        return Math.floor(this.userPoints / 100) + 1;
    }

    get userInitials(): string {
        return this.username.substring(0, 2).toUpperCase();
    }

    navLinks = [
        {label: 'Home', path: '/'},
        {label: 'Challenges', path: '/challenges'},
        {label: 'Leaderboard', path: '/leaderboard'}
    ];

    // Actions
    toggleDropdown() {
        this.isDropdownOpen = !this.isDropdownOpen;
    }

    onCreateChallenge() {
        this.challengeService.isCreateModalOpen.set(true);
        this.router.navigate(['/challenges']);
        this.isMenuOpen = false;
    }

    // Logout folosind Router
    onLogout() {
        this.authService.logout();
        this.isDropdownOpen = false;
        this.router.navigate(['/auth']); // Navigare directă prin Router
    }

    // CLICK OUTSIDE
    @HostListener('document:click', ['$event'])
    clickOutside(event: Event) {
        if (!this.elementRef.nativeElement.contains(event.target)) {
            this.isDropdownOpen = false;
        }
    }
}
