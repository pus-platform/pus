import { type ClassValue, clsx } from "clsx"
import { twMerge } from "tailwind-merge"

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

export const multiFormatDateString = (timestamp = "") => {
  const timestampNum = Math.round(new Date(timestamp).getTime() / 1000);
  const date = new Date(timestampNum * 1000);
  const now = new Date();

  const diff = now.getTime() - date.getTime();
  const diffInSeconds = diff / 1000;
  const diffInMinutes = diffInSeconds / 60;
  const diffInHours = diffInMinutes / 60;
  const diffInDays = diffInHours / 24;
  const diffInWeeks = diffInDays / 7;
  const diffInYears = diffInDays / 7;

  switch (true) {
    case Math.floor(diffInDays) >= 365:
      return `${Math.floor(diffInYears)}y`;
    case Math.floor(diffInDays) >= 7:
      return `${Math.floor(diffInWeeks)}w`;
    case Math.floor(diffInDays) >= 1:
      return `${Math.floor(diffInDays)}d`;
    case Math.floor(diffInHours) >= 1:
      return `${Math.floor(diffInHours)}h`;
    case Math.floor(diffInMinutes) >= 1:
      return `${Math.floor(diffInMinutes)}m`;
    default:
      return "Just now";
  }
};