import { useDeletePost } from "@/lib/react-query/queries/post";
import React, { useState, useEffect } from "react";
import { Pencil } from 'lucide-react';
import { Trash2 } from 'lucide-react';
import { useNavigate } from "react-router-dom";
import HashtagText from "../common/HashtagText/HashtagText";
import Card from "../common/Card/Card";
import Button from "../common/Button/Button";
import Modal from "../common/Modal/Modal";
import MyAvatar from "../common/MyAvatar/MyAvatar";
import { FaArrowLeftLong } from "react-icons/fa6";
import CommentCard from "./CommentCard";
import { useGetPostComments } from "@/lib/react-query/queries/comment";
import PostStats from "./PostStats";
import CommentForm from "./CommentForm";
import { Post } from "@/lib/types";
import axios from "axios";
import Cookies from "js-cookie";
import { useUserContext } from "@/context/AuthContext";

const PostDetails: React.FC<{ setClose: () => void, post: Post, isOpen: boolean }> = ({ isOpen, setClose, post }) => {
    const [isModalOpen, setIsModalOpen] = useState(isOpen);
    const [imageUrl, setImageUrl] = useState<string | null>(null);
    const { mutateAsync: deletePost } = useDeletePost({ id: post.id });
    const navigate = useNavigate();
    const { data: comments } = useGetPostComments({ post: post.id });
    const { user } = useUserContext();

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
            fetchImageUrl(post.imageUrl);
        }
    }, [post?.imageUrl]);

    const handleCloseModal = () => {
        setIsModalOpen(false);
        setClose();
    };

    return (
        <Modal isOpen={isModalOpen} onClose={handleCloseModal}>
            <div
                className={`flex md:grid  flex-col ${post?.imageUrl?.length > 1 ? "grid-cols-9 " : "grid-cols-4 "
                    } lg:max-w-[1100px] md:max-w-[700px] md:min-h-[580px] min-w-sm  
            w-sm md:w-[1000px]  md:max-h-[500px] h-full md:h-[590px] 
            overflow-hidden md:dark:bg-gray-900 dark:bg-black bg-white 
            dark:border-gray-800  border-gray-200 border  md:rounded-3xl min-h-screen shadow-lg`}
            >
                {imageUrl && (
                    <div className="md:col-span-4 flex dark:bg-black px-2 justify-center items-center flex-2 w-full h-1/2 md:h-full rounded-xl relative">
                        <Card imgSrc={{ url: imageUrl, name: post?.imageUrl }} />
                    </div>
                )}
                <div className="md:col-span-5 md:h-1/2  ">
                    <div className="p-4 h-full space-y-3">
                        <div className="flex justify-between">
                            <MyAvatar id={post.user.id} row hasName hasUsername size="sm" />
                            {
                                user.id === post.user.id &&
                                <div className="flex mt-4 gap-4">
                                    <Button
                                        onClick={() => navigate(`/edit-post/${post.id}`)}
                                        size="xs"
                                        color="transparentFit"
                                        label=""
                                        icon={<Pencil className="text-purple-500 h-5 w-5" />}
                                    />
                                    <Button
                                        onClick={() => {
                                            deletePost();
                                            setIsModalOpen(false);
                                        }}
                                        size="xs"
                                        color="transparentFit"
                                        label=""
                                        icon={<Trash2 color="#877EFF" className="h-5 w-5" />}
                                    />
                                </div>
                            }
                        </div>
                        <div className="text-left ml-2">
                            <HashtagText text={post?.caption} />
                        </div>
                        <>
                            <div>
                                <div className="max-h-[260px] md:h-[230px] md:max-h-[350px] overflow-y-auto scrollbar-hide ">
                                    {comments?.map((comment) => <CommentCard key={comment.id} comment={comment} />)}
                                </div>
                                <PostStats post={post} />
                                <CommentForm postId={post.id} />
                            </div>
                        </>
                    </div>
                </div>
            </div>
            <button className="md:hidden fixed top-0 left-0 p-2 text-black dark:text-white" onClick={handleCloseModal} >
                <FaArrowLeftLong className="h-6 w-6" />
            </button>
        </Modal>
    )
};

export default PostDetails;
