/**
 * The UserProfile interface is the central data aggregate for the application.
 * It combines core identity, progression metrics (XP/Level), and relational
 * data (Badges/Activity) into a unified DTO.
 */

import {Badge} from './badge.model';

/***
 * Orchestrates all user-centric data points required by the Profile and Dashboard views.
 */
export interface UserProfile {
  id: string;
  username: string;
  email: string;

  /** Total Experience Points (XP) accumulated by the user. */
  points: number;

  /** Calculated progression tier based on points (e.g., Level = points / 100). */
  level: number;

  /** Filename or URL for the profile picture; optional for new accounts. */
  avatar?: string;

  /** Aggregated count of all quests successfully finished. */
  totalCompletedChallenges: number;

  /** The number of consecutive days/weeks the user has been active. */
  currentStreak: number;

  /** Collection of unlocked Badge objects. */
  badges: Badge[];

  /** Chronological list of user interactions and quest completions. */
  recentActivity: ActivityHistory[];

  /** * A key-value map representing experience gained per category.
   * Example: { 'Fitness': 45, 'Coding': 80 }. Used for Radar/Bar charts.
   */
  skillBreakdown: { [key: string]: number };
}

/**
 * Represents a discrete event in the user's journey. Used to populate
 * the "Recent Activity" feed on the profile page.
 */
export interface ActivityHistory {
  /** The name of the challenge associated with this event. */
  challengeTitle: string;

  /** The result of the interaction (e.g., 'COMPLETED', 'ACCEPTED'). */
  status: string;

  /** * Timestamp of the event.
   * Technical Note: Typed as 'any' to handle Java LocalDateTime array formats.
   */
  date: any;

  /** Tracks how many times a recurring challenge has been finished. */
  timesCompleted: number;
}
