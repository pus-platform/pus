import React, { useState } from "react";
import { AiFillHeart, AiOutlineHeart } from "react-icons/ai";

interface LikeButtonProps {
  likes: number;
}

const LikeButton: React.FC<LikeButtonProps> = ({ likes }) => {
  const [liked, setLiked] = useState(false);
  const [likeCount, setLikeCount] = useState(likes);

  const handleLike = () => {
    setLiked(!liked);
    setLikeCount(liked ? likeCount - 1 : likeCount + 1);
  };

  return (
    <div className="flex items-center space-x-2">
      <button
        onClick={handleLike}
        className="flex items-center justify-center p-2 rounded-full focus:outline-none"
      >
        {liked ? (
          <AiFillHeart className="text-red-500 w-6 h-6" />
        ) : (
          <AiOutlineHeart className="text-purple-500 w-6 h-6" />
        )}
      </button>
      <span className="text-black text-sm dark:text-white">
        {likeCount} {likeCount === 1 ? "like" : "likes"}
      </span>
    </div>
  );
};

export default LikeButton;
