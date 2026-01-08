export enum LeaderboardRange {
  WEEKLY = 'WEEKLY',
  MONTHLY = 'MONTHLY',
  ALL_TIME = 'ALL_TIME'
}

export interface LeaderboardEntry {
  rank: number;
  username: string;
  avatar: string | null;
  totalPoints: number;
  isImageError?: boolean;
}
