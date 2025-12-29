export interface Badge {
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
