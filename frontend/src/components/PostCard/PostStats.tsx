import { Bookmark,BookmarkCheck } from 'lucide-react';
import Button from "../common/Button/Button";
import { AiFillHeart, AiOutlineHeart } from "react-icons/ai";
import { IoBookmark } from "react-icons/io5";
import { Post } from "@/lib/types";
import { useDeleteSavedPost, useGetSavedPosts, useSavePost } from "@/lib/react-query/queries/bookmark";
import { MessageSquareMore } from 'lucide-react';
import { useUserContext } from "@/context/AuthContext";
import { useDeletePostLike, useLikePost } from "@/lib/react-query/queries/postLike";
import { useState } from "react";
import { useGetPostComments } from "@/lib/react-query/queries/comment";
import Loader from "../common/Loader/Loader";

const PostStats: React.FC<{ post: Post, openDetailed?: () => void }> = ({ post, openDetailed }) => {
  const { data: bookmarks, isPending: isGetSavedPending } = useGetSavedPosts();
  const { data: comments, isPending: isGetCommentsPending } = useGetPostComments({ post: post.id });
  const { user } = useUserContext();
  const { mutateAsync: savePost } = useSavePost({ postId: post?.id });
  const { mutateAsync: unsavePost } = useDeleteSavedPost({ postId: post?.id });
  const { mutateAsync: likePost } = useLikePost({ postId: post?.id });
  const { mutateAsync: unlikePost } = useDeletePostLike({ postId: post?.id, userId: user.id });
  const [likes, setLikes] = useState<number>(post?.likes?.length)

  const [isPostBookmarked, setIsPostBookmarked] = useState<boolean>(bookmarks?.some((b) => b?.post.id === post?.id));
  const [isPostLiked, setIsPostLiked] = useState<boolean>(post?.likes?.some((l) => l?.user.id === user?.id))

  if (isGetSavedPending || isGetCommentsPending) return <Loader />;

  const handleSavePost = () => {
    if (isPostBookmarked) {
      setIsPostBookmarked(false)
      unsavePost({ postId: post?.id });
    } else {
      setIsPostBookmarked(true)
      savePost({ postId: post?.id });
    }
  };

  const handleLikePost = () => {
    if (isPostLiked) {
      unlikePost();
      setLikes((l) => l - 1)
      setIsPostLiked(false);
    } else {
      setIsPostLiked(true);
      setLikes((l) => l + 1)
      likePost({ postId: post?.id });
    }
  };

  return (
    <div className="flex items-center mt-4 justify-between">
      <div className="flex gap-3 md:gap-5 items-center">
        <button
          onClick={() => handleLikePost()}
          className="flex  gap-1 text-purple-500"
        >
          {isPostLiked ? (
            <AiFillHeart className="text-red-500 w-6 h-6" />
          ) : (
            <AiOutlineHeart className="text-purple-500 w-6 h-6" />
          )}
          {likes}
        </button>
        <div className="flex items-center space-x-2">
          <Button
            onClick={openDetailed}
            size="xs"
            color="transparentFit"
            label=""
            icon={
              <MessageSquareMore color="#877EFF" className="text-purple-500 h-5 w-5" />
          }
          />
          <span className="text-purple-500 text-sm">{comments?.length ? comments.length : 0}</span>
        </div>
      </div>
      <Button
        size="xs"
        color="transparentFit"
        label=""
        icon={
          !isPostBookmarked ? (
            <Bookmark className="text-purple-500 h-6 w-6" />
          ) : (
            <IoBookmark className="text-purple-500 h-6 w-6" />
              )
        }
        onClick={handleSavePost}
      />
    </div>
  )
}

export default PostStats;