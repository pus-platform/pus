import React, { useState, useEffect } from "react";
import TextInput from "../common/TextInput/TextInput";
import Stories from "react-insta-stories";
import { Story } from "react-insta-stories/dist/interfaces";
import Button from "../common/Button/Button";

import Reply from "../RenderStories/Tabs/Reply";
import Like from "../RenderStories/Tabs/Like";
import Viewer from "../RenderStories/Tabs/Viewer";
import { EllipsisVertical, Trash2, X, Reply as ReplyIcon, Heart, Eye, SendHorizonal } from 'lucide-react';
import { useUserContext } from "@/context/AuthContext";
import { Story as MyStory } from "@/lib/types";
import { useDeleteStory, useGetUserStories } from "@/lib/react-query/queries/story";
import { useDeleteStoryLike, useLikeStory } from "@/lib/react-query/queries/storyLike";
import { multiFormatDateString } from "@/lib/utils";
import { Reaction } from "@/lib/enums";
import Loader from "../common/Loader/Loader";
import axios from "axios";
import Cookies from "js-cookie";
import { useCreateStoryReply } from "@/lib/react-query/queries/storyReply";
import { useAddViewer } from "@/lib/react-query/queries/viewer";

const StoriesComponent: React.FC<{ userId: number, close: () => void }> = ({ userId, close }) => {
  const { user } = useUserContext();
  const { data: stories, isPending: isStoryLoading } = useGetUserStories({ userId: userId })
  const [storyInView, setStoryInView] = useState<MyStory | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [activeTab, setActiveTab] = useState<string | null>(null);
  const [replyValue, setReplyValue] = useState("");
  const [imageUrl, setImageUrl] = useState<string[] | null>(null);
  const { mutateAsync: likeStory } = useLikeStory({ story: storyInView?.id, userId: user?.id });
  const { mutateAsync: unlikeStory } = useDeleteStoryLike({ story: storyInView?.id, userId: user?.id });
  const { mutateAsync: replyStory } = useCreateStoryReply({ story: storyInView?.id })
  const { mutateAsync: deleteStory } = useDeleteStory({ id: storyInView?.id });
  const { mutateAsync: viewStory } = useAddViewer({ story: storyInView?.id })

  const getType = (url: string) => {
    const ext = url?.split(".").pop()
    if (ext === "png" || ext === "jpg" || ext === "jpeg" || ext === "svg")
      return "image";
    else
      return "video";
  }

  useEffect(() => {
    const fetchImages = async () => {
      try {
        const imageUrls = await Promise.all(
          stories?.map((story) =>
            axios.get(story?.imageUrl, {
              headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${Cookies.get("token")}`,
              },
              responseType: "blob",
            })
              .then((response) => URL.createObjectURL(response.data))
          )
        );
        setImageUrl(imageUrls);
      } catch (error) {
        console.error("Error fetching images:", error.message);
      }
    };

    if (stories?.length > 0) {
      fetchImages();
    }
  }, [stories]);

  const storyItems: Story[] = stories?.map((story, index) => {
    if (!imageUrl?.at(index) || !storyInView) return <Loader />
    return ({
      url: imageUrl.at(index),
      type: getType(story.imageUrl),
      header: {
        heading: story.user.fullname,
        subheading: `@${story.user.username}•${multiFormatDateString(story.createdAt)} `,
        profileImage: story.user.image,
      },
      id: story.id,
    })
  });

  const handleViewStory = (storyIndex: number) => {
    setStoryInView(stories.at(storyIndex));
  };

  const handleLikeStory = () => {
    if (!!storyInView) {
      console.log(storyInView?.likes)
      if (storyInView?.likes?.some((sl) => sl.user.id === user.id)) {
        likeStory()
        setStoryInView({
          ...storyInView,
          likes: storyInView?.likes?.filter((sl) => sl.user.id !== user.id)
        });
      }
      else {
        unlikeStory();
        setStoryInView({
          ...storyInView,
          likes: storyInView ? [...storyInView?.likes, { reaction: Reaction.LIKE, user: user }] : [],
        });
      }
    }
  }

  const handleToggleModal = () => {
    setIsModalOpen(!isModalOpen);
  };

  const handleTabClick = (tab: string) => {
    setActiveTab(tab);
  };

  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setReplyValue(event.target.value);
  };

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    replyStory({ replyContent: replyValue })
    setReplyValue("");
  };

  if (isStoryLoading || !imageUrl) return <Loader />

  return (
    <div className="relative mt-3 px-2 text-left">
      <Stories
        stories={storyItems}
        onStoryStart={handleViewStory}
        defaultInterval={3000}
        loader={<Loader />}
        width={500}
        height={750}
        onStoryEnd={viewStory}
        onAllStoriesEnd={close}
      />






      {storyInView?.user.id === user?.id && (
        <>
          {!isModalOpen ? (
            <div className="absolute z-[1000] right-3 top-6 cursor-pointer flex items-center gap-2">
              <button onClick={() => deleteStory}>
                <Trash2 color="#877EFF" className="h-5 w-5 text-purple-500" />
              </button>
              <button onClick={handleToggleModal}>
                <EllipsisVertical color="#877EFF" width={20} height={20} />
              </button>
            </div>
          ) : (
            <button
              className="absolute z-[1000] right-3 top-6 cursor-pointer"
              onClick={handleToggleModal}
            >
              <X color="#877EFF" width={20} height={20} />
            </button>
          )}
          {isModalOpen && (
            <div
              id="modal"
              className="absolute top-20 left-0 right-0 bottom-0 h-[80%] bg-black bg-opacity-50 flex justify-center z-[1100] animate-fadeIn"
            >
              <div className="rounded-lg w-80">
                <div className="flex bg-white dark:bg-black w-[150px] mx-auto justify-between p-3 rounded-full px-4 items-center mb-4 border border-purple-500">
                  <div
                    className={`flex flex-col items-center cursor-pointer ${activeTab === "reply"
                      ? "text-yellow-500"
                      : "text-purple-500"
                      }`}
                    onClick={() => handleTabClick("reply")}
                  >
                    <ReplyIcon width={20} height={20} color="#877EFF" />
                  </div>
                  <div
                    className={`flex flex-col items-center cursor-pointer ${activeTab === "like"
                      ? "text-yellow-500"
                      : "text-purple-500"
                      }`}
                    onClick={() => handleTabClick("like")}
                  >
                    <Heart width={20} height={20} color="#877EFF" />
                  </div>
                  <div
                    className={`flex flex-col items-center cursor-pointer ${activeTab === "view"
                      ? "text-yellow-500"
                      : "text-purple-500"
                      }`}
                    onClick={() => handleTabClick("view")}
                  >
                    <Eye width={20} height={20} color="#877EFF" />
                  </div>
                </div>
                <div
                  className={`mb-4 rounded-lg  mt-2 w-full overflow-y-auto scrollbar-hide bg-white dark:bg-black border border-gray-800 ${activeTab
                    ? "h-[90%]"
                    : "bg-transparent darK:bg-transparent border-0"
                    } py-2`}
                  style={{ maxHeight: "calc(100% - 3rem)" }}
                >
                  {activeTab === "reply" && (
                    <>
                      <Reply storyId={storyInView?.id} />
                    </>
                  )}
                  {activeTab === "like" && (
                    <div className="animate-slideIn dark:text-white">
                      <Like storyId={storyInView?.id} />
                    </div>
                  )}
                  {activeTab === "view" && (
                    <div className="animate-slideIn dark:text-white">
                      <Viewer storyId={storyInView?.id} />
                    </div>
                  )}
                </div>
              </div>
            </div>
          )}
        </>
      )}






      {storyInView?.user.id !== user?.id && (
        <form
          onSubmit={handleSubmit}
          className="flex items-center z-[1000] relative gap-1 w-full rounded-3xl -mt-10 h-20 dark:text-white bg-black px-4"
        >
          <div className="w-full ">
            <TextInput
              type="text"
              placeholder="Write a reply..."
              value={replyValue}
              onChange={handleInputChange}
            />
          </div>
          <Button
            type="submit"
            color="transparentFit"
            icon={<SendHorizonal width={20} height={20} color="#877EFF" />}
          />
          <button
            type="button"
            className="flex gap-1 text-purple-500"
            onClick={handleLikeStory}
          >
            {storyInView?.likes?.some((sl) => sl.user.id === user.id) ? (
              <Heart width={20} height={20} color="#877EFF" />
            ) : (
              <Heart width={20} height={20} color="#877EFF" />
            )}
            {storyInView?.likes?.length}
          </button>
        </form>
      )}
    </div>
  );
};

export default StoriesComponent;
