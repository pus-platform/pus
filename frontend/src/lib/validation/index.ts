import * as z from "zod";

// ============================================================
// USER
// ============================================================
export const SignupValidation = z.object({
    username: z.string().min(3, { message: "Username size should be between 3 and 50 characters." }).max(50, { message: "Username size should be between 3 and 50 characters." }),
    fullname: z.string().min(3, { message: "Full name size should be between 3 and 50 characters." }).max(50, { message: "Full name size should be between 3 and 50 characters." }),
    email: z.string().email({ message: "Invalid email address." }),
    password: z.string().min(8, { message: "Password must be at least 8 characters." }).max(120, { message: "Password maximum size is 120 characters." }),
});

export const LoginValidation = z.object({
    username: z.string().min(3, { message: "Username size should be between 3 and 50 characters." }).max(50, { message: "Username size should be between 3 and 50 characters." }),
    password: z.string().min(8, { message: "Password must be at least 8 characters." }).max(120, { message: "Password maximum size is 120 characters." }),
});

export const ProfileValidation = z.object({
    fullname: z.string().min(3, { message: "Full name size should be between 3 and 50 characters." }).max(50, { message: "Full name size should be between 3 and 50 characters." }),
    username: z.string().min(3, { message: "Username size should be between 3 and 50 characters." }).max(50, { message: "Username size should be between 3 and 50 characters." }),
    email: z.string().email({ message: "Invalid email address." }),
    bio: z.string().trim().min(1, { message: "Minimum 1 character." }).max(2200, { message: "Maximum 2,200 characters." }).optional(),
});

// ============================================================
// POST
// ============================================================
export const PostValidation = z.object({
    caption: z.string().min(5, { message: "Minimum 1 characters." }).max(2200, { message: "Maximum 500 characters." }),
    // view: z.enum(["public", "private", "community"]),
});

export const CommentValidation = z.object({
    content: z.string().min(1, { message: "Minimum 1 character." }).max(500, { message: "Maximum 500 characters." }),
});

export const SectionValidation = z.object({
    course: z.string().trim().min(1, { message: "Minimum 1 character." }).max(2200, { message: "Maximum 2,200 characters." }),
});

export const MajorValidation = z.object({
    major: z.string().trim().min(1, { message: "Minimum 1 character." }).max(2200, { message: "Maximum 2,200 characters." }),
});

// ============================================================
// COMMENT
// ============================================================
export const CommentDTOValidation = z.object({
    content: z.string().min(1, { message: "Minimum 1 character." }).max(500, { message: "Maximum 500 characters." }),
});

// ============================================================
// COMMENT LIKE
// ============================================================
export const CommentLikeValidation = z.object({
    reaction: z.string({ required_error: "Please choose a reaction." }),
});


// ============================================================
// MAJOR COMMUNITY
// ============================================================

export const MajorCommunityValidation = z.object({
    major: z.object({
        name: z.string(),
    }),
    year: z.number().min(2010, { message: "Please choose a valid year." }),
});

// ============================================================
// MESSAGE
// ============================================================
export const MessageValidation = z.object({
    messageContent: z.string().min(1, { message: "Minimum 1 character." }).max(500, { message: "Maximum 500 characters." }),
});

// ============================================================
// REPLY TO STORY
// ============================================================
export const ReplyToStoryValidation = z.object({
    replyContent: z.string().min(1, { message: "Minimum 1 character." }).max(500, { message: "Maximum 500 characters." }),
});

// ============================================================
// SECTION COMMUNITY
// ============================================================
export const SectionCommunityValidation = z.object({
    division: z.number({ required_error: "Please choose the section's division." }),
    year: z.number().min(2024, { message: "Please choose a valid year." }),
    semester: z.number().min(1).max(2, { message: "Please choose a valid semester." }),
    sectionGroup: z.number().optional(),
});

// ============================================================
// STORY
// ============================================================
export const StoryValidation = z.object({
    view: z.string({ required_error: "Please specify the story's view." }),
    community: z.object({
        name: z.string(),
    }).optional(),
    imageUrl: z.string({ required_error: "Please add the image URL." }),
});




export const VerificationValidation = z.object({
    code: z.string().length(6, "Code must be 6 characters long"),
    username: z.string().min(3, { message: "Username size should be between 3 and 50 characters." }).max(50, { message: "Username size should be between 3 and 50 characters." }),
  });
  
  export type VerificationCode = z.infer<typeof VerificationValidation>;
  
  