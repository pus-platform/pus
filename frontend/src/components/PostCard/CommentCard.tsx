import { Reply } from 'lucide-react';
import Button from "../common/Button/Button";
import { SendHorizontal } from 'lucide-react';
import MyAvatar from "../common/MyAvatar/MyAvatar";
import { AiFillHeart, AiOutlineHeart } from "react-icons/ai";
import TextInput from "../../components/common/TextInput/TextInput";
import { Comment, CommentLike } from "@/lib/types";
import { useDeleteCommentLike, useLikeComment } from "@/lib/react-query/queries/commentLike";
import { useCreateCommentReply } from "@/lib/react-query/queries/comment";
import { useState } from "react";
import { useUserContext } from "@/context/AuthContext";


const CommentCard: React.FC<{ comment: Comment }> = ({ comment }) => {

  const { mutateAsync: likeComment } = useLikeComment({ comment: comment.id })
  const { mutateAsync: unlikeComment } = useDeleteCommentLike({ comment: comment.id })
  const { mutateAsync: replyToComment } = useCreateCommentReply({ post: comment.post, commentId: comment.id })
  const [replyTo, setReplyTo] = useState<number | null>(null);
  const [replyText, setReplyText] = useState<string>('');
  const { user } = useUserContext();
  const [isCommentLiked, setIsCommentLiked] = useState<boolean>(comment?.reactions?.some((l) => l?.user.id === user?.id))
  const [commentLikes, setCommentLike] = useState<CommentLike[]>(comment.reactions);

  const handleLikeComment = (event: React.MouseEvent<HTMLButtonElement>, commentId: number) => {
    event.preventDefault();
    if (isCommentLiked) {
      unlikeComment({ postId: comment.post, comment: commentId, userId: user.id });
      setIsCommentLiked(false);
      setCommentLike(commentLikes.filter((like) => like.user.id !== user.id));
    } else {
      likeComment({ postId: comment.post, comment: commentId, userId: user.id });
      setIsCommentLiked(true);
      setCommentLike([...commentLikes, { user: user }]);
    }
  }

  const handleReplyComment = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (replyText.trim()) {
      if (replyTo) {
        const replyTextWithUsername = `${'@' + comment.user.username + ' ' + replyText}`;
        replyToComment({
          repliedComment: replyTo,
          content: replyTextWithUsername
        });
        setReplyTo(null);
        setReplyText("");
      }
    }
  };

  return ((
    <div key={comment.id} className="py-2">
      <div className="flex justify-between">
        <div className="dark:text-white flex items-start gap-2 mb-6">
          <MyAvatar id={comment.user.id} row size="xs" start />
          <p className="dark:text-white w-full">{comment.content}</p>
        </div>
        <button
          onClick={(event) => handleLikeComment(event, comment.id)}
          className="text-purple-500"
        >
          {isCommentLiked ? (
            <AiFillHeart className="text-red-500 w-6 h-6" />
          ) : (
            <AiOutlineHeart className="text-purple-500 w-6 h-6" />
          )}
          {commentLikes.length}
        </button>
      </div>
      <div className="flex items-center gap-1 ml-14 -mt-5 mb-2">
        <button className="flex gap-1 dark:text-white text-xs cursor-pointer"
          onClick={() => setReplyTo(comment.id)}
        >
          <>
            <Reply color="#877EFF" className="h-5 w-5" />
          </>
        </button>
      </div>
      {replyTo === comment.id && (
        <form
          onSubmit={handleReplyComment}
          className="flex relative items-center mt-3 w-ful px-2 "
        >
          <div className="w-full">
            <TextInput
              type="text"
              value={replyText}
              onChange={(e) => setReplyText(e.target.value)}
              placeholder="Write a reply..."
            />
          </div>
          <div className="absolute right-3.5 top-1/2 -translate-y-1/2">
            <Button
              type="submit"
              size="xs"
              color="transparentFit"
              label=""
              icon={
                <SendHorizontal color="#877EFF" className="text-yellow-500 h-5 w-5" />
              }
            />
          </div>
        </form>
      )}
      {comment.replies && (
        <div className="pl-4 ">
          {comment.replies.map((reply) => (
            <div key={reply.id} className=" pt-2">
              <div className="flex justify-between">
                <div className="dark:text-white flex items-start py-2 gap-2">
                  <MyAvatar id={reply.user.id} row size="xs" start />
                  <p className="dark:text-white">
                    {reply.content}
                  </p>
                </div>
                <button
                  onClick={(event) => handleLikeComment(event, reply.id)}
                  className="text-purple-500"
                >
                  {isCommentLiked ? (
                    <AiFillHeart className="text-red-500 w-6 h-6" />
                  ) : (
                    <AiOutlineHeart className="text-purple-500 w-6 h-6" />
                  )}
                  {reply.reactions.length}
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
  )
}

export default CommentCard