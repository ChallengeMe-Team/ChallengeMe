import { Badge } from './badge.model'; // Referință la badge.model.ts existent

export interface UserProfile {
  id: string;
  username: string;
  email: string;
  points: number;
  level: number;
  avatar?: string;
  completedChallengesCount: number;
  currentStreak: number;
  badges: Badge[];
  recentActivity: ActivityHistory[];
  skillBreakdown: { [key: string]: number };
}

export interface ActivityHistory {
  challengeTitle: string;
  status: string;
  date: string;
}
