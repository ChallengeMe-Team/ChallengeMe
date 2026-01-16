/**
 * These structures define the data contract for the competitive ranking system.
 * They facilitate temporal filtering and provide the necessary metadata to render
 * the podium and ranking tables.
 */

/**
 * Defines the temporal scope for XP calculation. This allows the system
 * to generate dynamic rankings based on specific time windows.
 */
export enum LeaderboardRange {
  WEEKLY = 'WEEKLY',               // Resets every 7 days; emphasizes recent activity.
  MONTHLY = 'MONTHLY',             // Monthly aggregate for consistent performers.
  LAST_6_MONTHS = 'LAST_6_MONTHS', // Mid-term ranking for seasonal tracking.
  ALL_TIME = 'ALL_TIME'            // Cumulative XP since account creation.
}

/**
 * Represents a single row in the global or filtered rankings.
 * This model is consumed by the LeaderboardComponent to render both the
 * top-3 podium and the detailed list.
 */
export interface LeaderboardEntry {
  /** The calculated position of the user based on points within the selected range. */
  rank: number;

  /** The unique identifier for the player. */
  username: string;

  /** * The filename or URL of the user's profile picture.
   * Nullable if the user uses a default initial-based avatar.
   */
  avatar: string | null;

  /** The total Experience Points (XP) accumulated within the specified range. */
  totalPoints: number;

  /** * Optional flag used by the UI to handle broken image links gracefully
   * by switching to a fallback initial-based avatar.
   */
  isImageError?: boolean;
}
