import React, { useState, useEffect } from "react";
import HashtagText from "../common/HashtagText/HashtagText";
import Card from "../common/Card/Card";
import MyAvatar from "../common/MyAvatar/MyAvatar";
import { Post } from "@/lib/types";
import PostDetails from "./PostDetails";
import CommentForm from "./CommentForm";
import PostStats from "./PostStats";
import axios from "axios";
import Cookies from "js-cookie";

const PostCard: React.FC<{ post: Post }> = ({ post }) => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [imageUrl, setImageUrl] = useState<string | null>(null);

  const handleOpenModal = () => setIsModalOpen(true);
  const handleCloseModal = () => setIsModalOpen(false);

  const fetchImageUrl = async (url: string) => {
    try {
      const response = await axios.get(url, {
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${Cookies.get("token")}`,
        },
        responseType: "blob",
      });
      setImageUrl(URL.createObjectURL(response.data));
    } catch (error) {
      console.error("Error fetching image:", error.message);
    }
  };

  useEffect(() => {
    if (post?.imageUrl?.length > 0) {
      fetchImageUrl(post?.imageUrl);
    }
  }, [post?.imageUrl]);

  return (
    <div className="sm:max-w-screen-sm space-y-8 md:space-y4 p-5 lg:px-[26px] min-w-[300px] md:min-w-[500px] md:max-w-[550px] 
    rounded-[30px] dark:bg-gray-900 border border-purple-200 h-fit dark:border-gray-800">
      <div className="flex justify-between items-start">
        <MyAvatar id={post?.user?.id} hasName hasUsername size="lg" />
      </div>
      <div onClick={handleOpenModal} className="hover:cursor-pointer">
        <HashtagText text={post?.caption} />
      </div>
      {imageUrl && (
        <div onClick={handleOpenModal}>
          <Card imgSrc={{ url: imageUrl, name: post?.imageUrl }} />
        </div>
      )}
      <div className="space-y-3">
        <PostStats post={post} openDetailed={handleOpenModal} />
        <CommentForm postId={post?.id} />
      </div>
      {isModalOpen && <PostDetails isOpen={isModalOpen} post={post} setClose={handleCloseModal} />}
    </div>
  );
};

export default PostCard;
