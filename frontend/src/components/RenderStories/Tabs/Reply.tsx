import React, { useState } from "react";
import Avatar from "../../common/Avatar/Avatar";
import { BsFillReplyFill } from "react-icons/bs";
import { useUserContext } from "@/context/AuthContext";
import { useGetStoryReplies } from "@/lib/react-query/queries/storyReply";

const Reply: React.FC<{storyId: number}> = ({ storyId }) => {
  const [openReplyIndex, setOpenReplyIndex] = useState<number>(0);
  const {user} = useUserContext();
  const { data: userReply } = useGetStoryReplies({ story: storyId });

  const handleToggleContent = (id: number) => {
    if (openReplyIndex === id) {
      setOpenReplyIndex(null);
    } else {
      setOpenReplyIndex(id);
    }
  };

  return (
    <>
      {userReply.map((reply) => (
        <div
          key={reply.id}
          className="animate-slideIn dark:text-white "
          onClick={() => handleToggleContent(reply.id)}
        >
          <div className="p-4 space-y-2 md:space-y-4 w-full">
            <div
              className={`flex ${
                openReplyIndex === reply
                  ? "flex-col gap-4 text-xs"
                  : "flex-row "
              }  rounded-md`}
            >
              <div className="flex items-center gap-4 rounded-md">
                <span className="text-2xl mt-3">
                  //TODO icon
                  <BsFillReplyFill className="text-purple-500 w-4 h-4" />
                </span>
                <div>
                  <div className="flex text-gray-500 gap-2 dark:text-gray-100">
                    <Avatar
                    id={user.id}
                      row
                      start
                      onChat
                      imgSrc={user.imageUrl}
                      name={user.fullname}
                      size="sm"
                    />
                  </div>
                  <p className="text-gray-500 dark:text-gray-400 text-xs ml-[68px] -mt-6">
                    1d ago
                  </p>
                </div>
              </div>
              <p className="text-left text-purple-300 font-bold mb-2 mt-2 whitespace-pre-wrap	 break-words">
                {openReplyIndex === reply ? reply.replyContent : ""}
              </p>
            </div>
          </div>
        </div>
      ))}
    </>
  );
};

export default Reply;
