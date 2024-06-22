import { useState, useRef, useEffect } from "react";
import { MdArrowForwardIos } from "react-icons/md";
import Modal from "../../components/common/Modal/Modal";
import StoriesComponent from "../../components/Stories/Stories";
import Avatar from "../common/Avatar/Avatar";
import { ChevronRight, CirclePlus } from 'lucide-react';

import { Link, useNavigate } from "react-router-dom";
import { useUserContext } from "@/context/AuthContext";
import { useGetFollowingStories, useGetUserStories } from "@/lib/react-query/queries/story";
import { useGetFollowing } from "@/lib/react-query/queries/follower";
import { Story } from "@/lib/types";

const RenderStories = () => {
  const { user } = useUserContext();
  const [selectedUserId, setSelectedUserId] = useState<number | null>(null);
  const [story, setStory] = useState<Story[]>();
  const { data: Users } = useGetFollowing({ followerId: user?.id })
  const scrollContainerRef = useRef(null);
  const { data: followingStories } = useGetFollowingStories({ userId: user?.id });
  const { data: userStories } = useGetUserStories({ userId: user?.id });
  const stories = followingStories?.concat(userStories);
  const [isModalOpen, setModalOpen] = useState(false);
  const navigate = useNavigate();

  const usersWithStories = Users?.filter((user) =>
    stories?.some((story) => story?.user.id === user?.followed.id)
  );
  const scrollLeft = () => {
    if (scrollContainerRef.current) {
      scrollContainerRef.current.scrollBy({
        left: -200,
        behavior: "smooth",
      });
    }
  };

  const handleCloseModal = () => {
    setModalOpen(false);
    setSelectedUserId(null);
  };

  const scrollRight = () => {
    if (scrollContainerRef.current) {
      scrollContainerRef.current.scrollBy({
        left: 200,
        behavior: "smooth",
      });
    }
  };

  const handleClick = (userId: number) => {
    setSelectedUserId(userId);
    const userStories = stories?.filter((story) => story?.user.id === userId);
    if (userStories?.length > 0) {
      setStory(userStories);
      setModalOpen(true);
    } else if (user?.id === userId) {
      navigate("/create-story");
    }
  };

  return (
    <div>
      <div className="relative w-full flex align-center">
        <button
          onClick={scrollLeft}
          className="absolute h-6 w-6 hidden md:flex items-center justify-center left-0 top-1/4 dark:bg-gray-700 p-2 rounded-full shadow-md"
        >
          <ChevronRight color="#877EFF" width={12} height={12} />
        </button>
        <div
          className="flex justify-start items-center gap-[10px] w-full overflow-x-auto"
          ref={scrollContainerRef}
        >
          {user && (
            <div className="relative">
              <div
                className="relative"
                onClick={() => handleClick(user?.id)}
              >
                <Avatar
                  id={user?.id}
                  imgSrc={"src/assets/icons/profile-placeholder.svg"}
                  hasName={false}
                  size="lg"
                  col
                  onChat
                  row={false}
                  border
                  onClick={() => handleClick(user?.id)}
                />
              </div>
              <Link to="/create-story">
                <CirclePlus className="text-[#877EFF] absolute bottom-6 left-[42px] md:left-[42px] lg:left-[42px] rounded-full text-xl lg:text-2xl dark:bg-black bg-white" />
              </Link>
            </div>
          )}
          {usersWithStories?.map((follow) => (
            <div key={follow.followed.id} onClick={() => handleClick(follow?.followed?.id)}>
              <Avatar
                id={follow?.followed?.id}
                imgSrc={"src/assets/icons/profile-placeholder.svg"}
                hasName={false}
                size="lg"
                col
                onChat
                row={false}
                border
                onClick={() => handleClick(follow?.followed?.id)}
              />
            </div>
          ))}
        </div>
        <button
          onClick={scrollRight}
          className="absolute h-6 w-6 hidden md:flex items-center justify-center right-0 top-1/4 dark:bg-gray-700 p-2 rounded-full shadow-md"
        >
          <MdArrowForwardIos className="h-3 w-3 text-[#877EFF]" />
        </button>
      </div>
      <Modal isOpen={isModalOpen} onClose={handleCloseModal}>
        {story && <StoriesComponent close={handleCloseModal} userId={selectedUserId} />}
      </Modal>
    </div>
  );
};

export default RenderStories;
