export interface Badge {
  criteria: string;
  id: string;
  name: string;
  description: string;
  iconUrl: string;
  pointsReward: number;
}

export interface BadgeDisplay {
  badge: Badge;
  isUnlocked: boolean;
}
