import { Badge } from './badge.model';

export interface UserProfile {
  id: string;
  username: string;
  email: string;
  points: number;
  level: number;
  avatar?: string;
  totalCompletedChallenges: number;
  currentStreak: number;
  badges: Badge[];
  recentActivity: ActivityHistory[];
  skillBreakdown: { [key: string]: number };
}

export interface ActivityHistory {
  challengeTitle: string;
  status: string;
  date: any;
  timesCompleted: number;
}
