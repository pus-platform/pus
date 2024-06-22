import { useGetStoryLikes } from "@/lib/react-query/queries/storyLike";
import Avatar from "../../common/Avatar/Avatar";
import { FaHeart } from "react-icons/fa";
import { useUserContext } from "@/context/AuthContext";

const Like: React.FC<{ storyId: number }> = ({ storyId }) => {
  const { user } = useUserContext();
  const { data: userLike } = useGetStoryLikes({ story: storyId });

  return (
    <>
      {userLike.map(() => (
        <div className="animate-slideIn">
          <div className=" p-4 space-y-2 md:space-y-4 w-full">
            <div className="flex items-center gap-4  rounded-md">
              <span className="text-2xl mt-4 ">
                //TODO icon
                <FaHeart className="text-purple-500 w-4 h-4" />
              </span>
              <div>
                <div className="flex text-gray-500 gap-2 dark:text-gray-100">
                  <Avatar
                    id={user.id}
                    row
                    start
                    imgSrc={user.imageUrl}
                    name={user.fullname}
                    size="sm"
                  />
                </div>
                <p className="text-gray-500 dark:text-gray-400 text-xs ml-[68px] -mt-6">
                  @{user.username}
                </p>
              </div>
            </div>
          </div>
        </div>
      ))}
    </>
  );
};

export default Like;
