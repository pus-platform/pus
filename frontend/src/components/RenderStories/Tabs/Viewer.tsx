import { MdRemoveRedEye } from "react-icons/md";
import Avatar from "../../common/Avatar/Avatar";
import { useUserContext } from "@/context/AuthContext";
import { useGetStoryViews } from "@/lib/react-query/queries/viewer";

const Viewer: React.FC<{ storyId: number }> = ({ storyId }) => {
  const { user } = useUserContext();
  const { data: userViewers } = useGetStoryViews({ story: storyId });

  return (
    <>
      {userViewers.map((userViewer) => (
        <div key={userViewer.story} className="animate-slideIn">
          <div className="p-4 space-y-2 md:space-y-4 w-full">
            <div className="flex items-center gap-4  rounded-md">
              <span className="text-2xl mt-4 ">
                // TODO: icon
                <MdRemoveRedEye className="text-purple-500 w-4 h-4" />
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
                <p className="text-gray-500 dark:text-gray-400 text-xs ml-12  -mt-8">
                  1d ago
                </p>
              </div>
            </div>
          </div>
        </div>
      ))}
    </>
  );
};

export default Viewer;
