import { Reaction, University, Gender, GroupRole, MessageReceiver, NotificationType, View, Status } from "./enums";

export interface Bookmark {
    user?: ShortUser;
    post?: Post;
    postId?: number;
    savedAt?: string;
    links?: object;
}

export interface SignupUser {
    fullname: string;
    username: string;
    email: string;
    password: string;
}

export interface LoginUser {
    username: string;
    password: string;
}


export interface Comment {
    id?: number;
    user?: ShortUser;
    post?: number;
    content?: string;
    commentedAt?: string;
    repliedComment?: number | null;
    reactions?: CommentLike[];
    replies?: Comment[];
    commentId?: number;
    links?: object;
}

export interface CommentLike {
    user?: ShortUser;
    comment?: number;
    likedAt?: string;
    reaction?: Reaction;
    userId?: number;
    postId?: number;
    links?: object;
}

export interface Community {
    id?: number;
    name?: University;
    imageUrl?: string;
    links?: object;
}

export interface Follower {
    follower?: ShortUser;
    followed?: ShortUser;
    followedAt?: string;
    followedId?: number;
    followerId?: number;
    links?: object;
}

export interface GroupChat {
    id?: number;
    name?: string;
    createdAt?: string;
    messages?: Message[];
    links?: object;
}

export interface GroupMember {
    user?: ShortUser;
    group?: number;
    role?: GroupRole;
    memberSince?: string;
    userId?: number;
    links?: object;
}

export interface MajorCommunity {
    id?: number;
    major?: string;
    year?: number;
    majorGroup?: number;
    links?: object;
}

export interface Message {
    id?: number;
    messageContent?: string;
    sentAt?: string;
    isRead?: boolean;
    receiverUser?: ShortUser;
    receiverGroup?: number;
    receiverType?: MessageReceiver;
    sender?: ShortUser;
    senderId?: number;
    userId?: number;
    groupId?: number;
    links?: object;
}

export interface Notification {
    id?: number;
    content?: string;
    user?: ShortUser;
    notificationType?: NotificationType;
    notifiedAt?: string;
    links?: object;
}

export interface Post {
    id?: number;
    user?: ShortUser;
    community?: University | null;
    createdAt?: string;
    location?: string;
    caption?: string;
    imageUrl?: string;
    view?: View;
    likes?: PostLike[];
    comments?: Comment[];
    photoOrVideoAltText?: string;
    userId?: number;
    links?: object;
}

export interface PostLike {
    user?: ShortUser;
    post?: ShortPost;
    likedAt?: string;
    reaction?: Reaction;
    userId?: number;
    postId?: number;
    links?: object;
}

export interface ReplyToStory {
    id?: number;
    replyContent?: string;
    user?: ShortUser;
    story?: number;
    message?: number;
    repliedAt?: string;
    links?: object;
}

export interface SectionCommunity {
    id?: number;
    course?: string;
    division?: number;
    year?: number;
    semester?: number;
    sectionGroup?: number;
    userId?: number;
    links?: object;
}

export interface ShortPost {
    id?: number;
    user?: ShortUser;
    community?: University | null;
    createdAt?: string;
    location?: string;
    caption?: string;
    view?: View;
    links?: object;
}

export interface ShortUser {
    id?: number;
    image?: string;
    username?: string;
    fullname?: string;
    isActive?: Status;
    links?: object;
}

export interface Story {
    id?: number;
    user?: ShortUser;
    createdAt?: string;
    view?: View;
    community?: University | null;
    imageUrl?: string;
    image?: any;
    type?: string;
    header?: object;
    userId?: number;
    likes?: StoryLike[];
    links?: object;
}

export interface StoryLike {
    user?: ShortUser;
    story?: number;
    reaction?: Reaction;
    likedAt?: string;
    userId?: number;
    links?: object;
}

export interface User {
    id?: number;
    username?: string;
    fullname?: string;
    dob?: string;
    email?: string;
    imageUrl?: string;
    password?: string;
    isActive?: Status;
    bio?: string;
    gender?: Gender;
    community?: University;
    followers?: Follower[];
    following?: Follower[];
    stories?: Story[];
    bookmarks?: Bookmark[];
    likes?: PostLike[];
    links?: object;
}

export interface Viewer {
    user?: ShortUser;
    story?: number;
    viewedAt?: string;
    links?: object;
}