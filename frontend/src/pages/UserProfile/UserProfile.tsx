import { useNavigate, useParams } from "react-router-dom";
import Button from "../../components/common/Button/Button";
import { Pencil } from 'lucide-react';
import PostList from "../../components/PostList/PostList";
import { useMediaQuery } from "@mui/material";
import { LiaPhotoVideoSolid } from "react-icons/lia";
import { useEffect, useState } from "react";
import Modal from "../../components/common/Modal/Modal";
import MyAvatar from "../../components/common/MyAvatar/MyAvatar";
import { useGetUserPosts } from "@/lib/react-query/queries/post";
import { useGetUserById } from "@/lib/react-query/queries/user";
import { useAddFollower, useDeleteFollower, useGetFollowers, useGetFollowing } from "@/lib/react-query/queries/follower";
import { Trash } from "lucide-react";
import { useUserContext } from "@/context/AuthContext";
import Loader from "@/components/common/Loader/Loader";

const UserProfile = () => {
  const navigate = useNavigate();
  const { id } = useParams<string>();
  const { data: user, isPending: isUserLoading } = useGetUserById({ id: parseInt(id) })
  const { user: curUser } = useUserContext();
  const { data: posts, isPending: isPostLoading } = useGetUserPosts({ userId: user?.id });
  const { data: following } = useGetFollowing({ followerId: user?.id })
  const { data: followers } = useGetFollowers({ followedId: user?.id })
  const { data: curFollowing } = useGetFollowing({ followerId: curUser?.id })
  const { data: curFollowers } = useGetFollowers({ followedId: curUser?.id })
  const [followersNumber, setFollowersNumber] = useState<number>(user?.followers?.length || 0)
  const [followingNumber, setFollowingNumber] = useState<number>(user?.following?.length || 0)
  const { mutateAsync: follow } = useAddFollower()
  const { mutateAsync: unfollow } = useDeleteFollower()
  const isMobile = useMediaQuery("(max-width:600px)");
  const [isFollowingModalOpen, setFollowingModalOpen] = useState(false);
  const [isFollowersModalOpen, setFollowersModalOpen] = useState(false);

  useEffect(() => {
    setFollowersNumber(followers?.length || 0)
    setFollowingNumber(following?.length || 0)
  }, [following, followers])

  const handleRemoveFollow = async ({ followerId, followedId }) => {
    await unfollow({ followerId, followedId });
    if (curUser?.id === user?.id)
      setFollowersNumber(followersNumber - 1);
  }

  const handleFollow = async ({ followerId, followedId }) => {
    await follow({ followerId, followedId });
    if (curUser?.id === user?.id)
      setFollowingNumber(followingNumber + 1);
  }

  const handleUnfollow = async ({ followerId, followedId }) => {
    await unfollow({ followerId, followedId });
    if (curUser?.id === user?.id)
      setFollowingNumber(followingNumber - 1);
  }


  if (isUserLoading || isPostLoading) {
    return <Loader />
  }
  const handleCloseFollowingModal = () => setFollowingModalOpen(false);
  const handleCloseFollowersModal = () => setFollowersModalOpen(false);
  const handleOpenFollowingModal = () => setFollowingModalOpen(true);
  const handleOpenFollowersModal = () => setFollowersModalOpen(true);
  const handleEditProfile = () => navigate(`/edit-profile/`);


  return (
    <div className="py-10 px-3 lg:px-[60px] lg:pb-[80px] dark:text-white min-h-screen w-full">
      <div className="flex space-y-4 justify-center items-center  lg:justify-between w-full md:items-start ">
        <MyAvatar
          id={user?.id}
          hasName={true}
          hasUsername={true}
          size={isMobile ? "xl" : "2xl"}
          start={!isMobile}
        />
        <div className="flex justify-between space-x-4">
          {curUser?.id === parseInt(id) ? (
            <Button
              onClick={handleEditProfile}
              size="md"
              color="blackFit"
              icon={<Pencil />}
              label="Edit Profile"
            />
          ) : (

            curFollowing?.some((following) => following?.followed?.id === user?.id) ?
              (
                <div className="gap-4 w-full flex flex-row-reverse">
                  <button
                    className="px-5 py-2 flex items-center justify-center rounded-lg border border-gray-300 dark:border-gray-700 bg-gray-100 dark:bg-gray-900 text-sm font-[600] dark:text-white"
                    onClick={() => handleUnfollow({ followerId: curUser?.id, followedId: user?.id })}>
                    Following
                  </button>
                  {
                    curFollowers?.some((follow) => follow?.follower?.id === user?.id) &&
                    <button className="px-5 py-2 flex items-center justify-center rounded-lg bg-red text-sm font-[600] text-white"
                      onClick={() => handleRemoveFollow({ followerId: user?.id, followedId: curUser?.id })}>
                      <Trash />
                    </button>
                  }
                </div>
              )
              : curFollowers?.some((follower) => follower?.follower?.id === user?.id) ?
                (
                  <div className="gap-4 w-full flex flex-row-reverse">
                    <button className="px-5 py-2 flex items-center justify-center rounded-lg bg-blue-500 text-sm font-[600] text-white"
                      onClick={() => handleFollow({ followerId: curUser?.id, followedId: user?.id })}>
                      Follow Back
                    </button>
                    <button className="px-5 py-2 flex items-center justify-center rounded-lg bg-red text-sm font-[600] text-white"
                      onClick={() => handleRemoveFollow({ followerId: user?.id, followedId: curUser?.id })}>
                      <Trash />
                    </button>
                  </div>
                ) :
                (
                  <div className="gap-4 w-full flex flex-row-reverse">
                    <button className="px-5 py-2 flex items-center justify-center rounded-lg bg-blue-500 text-sm font-[600] text-white"
                      onClick={() => handleFollow({ followerId: curUser?.id, followedId: user?.id })}>
                      Follow
                    </button>
                  </div>
                )
          )}
        </div>
      </div>
      <div className="md:ml-44 space-y-6  md:-mt-8">
        <div className="flex space-x-1 justify-center md:justify-start md:space-x-8">
          <p className="flex space-x-2 text-lg font-medium dark:text-white">
            <span className="text-purple-500">{posts?.length || 0}</span>
            <span>Post</span>
          </p>
          <button
            onClick={handleOpenFollowersModal}
            className="flex space-x-2 text-lg font-medium dark:text-white"
          >
            <span className="text-purple-500">{followersNumber}</span>
            <span>Followers</span>
          </button>
          <button
            onClick={handleOpenFollowingModal}
            className="flex space-x-2 text-lg font-medium dark:text-white"
          >
            <span className="text-purple-500">{followingNumber}</span>
            <span>Following</span>
          </button>
        </div>
        <p className="max-w-[320px]">{user?.bio}</p>
      </div>
      <div className="space-y-6">
        <div className="flex my-6 md:my-8 lg:my-10">
          <button className="flex items-center justify-center gap-[10px] py-[12px] px-[12px] md:px-[50px] border dark:border-[#101012] dark:bg-[#101012] bg-gray-100 dark:text-white rounded-[10px]">
            <svg
              width="20"
              height="20"
              viewBox="0 0 20 20"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
            >
              <g clipPath="url(#clip0_1_1450)">
                <path
                  d="M1.66675 9.99996C1.66675 6.07159 1.66675 4.1074 2.88714 2.88701C4.10752 1.66663 6.07171 1.66663 10.0001 1.66663C13.9285 1.66663 15.8926 1.66663 17.113 2.88701C18.3334 4.1074 18.3334 6.07159 18.3334 9.99996C18.3334 13.9283 18.3334 15.8925 17.113 17.1129C15.8926 18.3333 13.9285 18.3333 10.0001 18.3333C6.07171 18.3333 4.10752 18.3333 2.88714 17.1129C1.66675 15.8925 1.66675 13.9283 1.66675 9.99996Z"
                  stroke="#877EFF"
                  strokeWidth="1.5"
                />
                <circle
                  cx="13.3334"
                  cy="6.66667"
                  r="1.66667"
                  stroke="#877EFF"
                  strokeWidth="1.5"
                />
                <path
                  d="M1.66675 10.4168L3.1264 9.13963C3.8858 8.47517 5.03031 8.51328 5.74382 9.22679L9.31859 12.8016C9.89128 13.3743 10.7928 13.4523 11.4554 12.9866L11.7039 12.812C12.6574 12.1419 13.9475 12.2195 14.8138 12.9992L17.5001 15.4168"
                  stroke="#877EFF"
                  strokeWidth="1.5"
                  strokeLinecap="round"
                />
              </g>
              <defs>
                <clipPath id="clip0_1_1450">
                  <rect width="20" height="20" fill="white" />
                </clipPath>
              </defs>
            </svg>
            <span className="text-[16px] font-[500] leading-[140%]">Posts</span>
          </button>
        </div>
        <div className="pb-10 flex justify-center">
          {posts?.length > 0 ? (
            <PostList posts={posts} />
          ) : (
            //TODO: icon
            <div className="flex justify-center items-center h-full py-28 w-full">
              <LiaPhotoVideoSolid className="text-purple-300 h-24 w-24 " />
            </div>
          )}
        </div>
      </div>
      <Modal isOpen={isFollowersModalOpen} onClose={handleCloseFollowersModal}>
        <div className="min-w-[400px] h-screen md:w-[600px] sm:h-[450px] dark:bg-gray-800 bg-gray-100 rounded-lg dark:text-white">
          <h2 className="font-[600] w-full flex items-center justify-center border-b-2 border-gray-700 p-4">Following</h2>
          <div className="max-h-[70%] overflow-y-scroll flex flex-col gap-4 p-4">
            {followers?.map((oneFollow) => (
              <div key={oneFollow?.follower?.id} className=" w-full flex items-center justify-between">
                <MyAvatar
                  id={oneFollow?.follower?.id}
                  hasName
                  hasUsername
                  onClick={() => setFollowersModalOpen(false)}
                  size="lg"
                />
                {
                  oneFollow?.follower?.id !== curUser?.id &&
                  (
                    curFollowing?.some((following) => following?.followed?.id === oneFollow?.follower?.id)
                      ? (
                        <div className="gap-4 w-full flex flex-row-reverse">
                          <button
                            className="px-5 py-2 flex items-center justify-center rounded-lg border border-gray-300 dark:border-gray-700 bg-gray-100 dark:bg-gray-900 text-sm font-[600] dark:text-white"
                            onClick={() => handleUnfollow({ followerId: user?.id, followedId: oneFollow?.follower?.id })}>
                            Following
                          </button>
                          <button className="px-5 py-2 flex items-center justify-center rounded-lg bg-red text-sm font-[600] text-white"
                            onClick={() => handleRemoveFollow({ followerId: oneFollow?.follower?.id, followedId: user?.id })}>
                            <Trash />
                          </button>
                        </div>
                      )
                      : curFollowers?.some((follower) => follower?.follower?.id === oneFollow?.follower?.id) ?
                        (
                          <div className="gap-4 w-full flex flex-row-reverse">
                            <button className="px-5 py-2 flex items-center justify-center rounded-lg bg-blue-500 text-sm font-[600] text-white"
                              onClick={() => handleFollow({ followerId: user?.id, followedId: oneFollow?.follower?.id })}>
                              Follow Back
                            </button>
                            <button className="px-5 py-2 flex items-center justify-center rounded-lg bg-red text-sm font-[600] text-white"
                              onClick={() => handleRemoveFollow({ followerId: oneFollow?.follower?.id, followedId: user?.id })}>
                              <Trash />
                            </button>
                          </div>
                        ) :
                        (
                          <div className="gap-4 w-full flex flex-row-reverse">
                            <button className="px-5 py-2 flex items-center justify-center rounded-lg bg-blue-500 text-sm font-[600] text-white"
                              onClick={() => handleFollow({ followerId: user?.id, followedId: oneFollow?.follower?.id })}>
                              Follow Back
                            </button>
                            <button className="px-5 py-2 flex items-center justify-center rounded-lg bg-red text-sm font-[600] text-white"
                              onClick={() => handleRemoveFollow({ followerId: oneFollow?.follower?.id, followedId: user?.id })}>
                              <Trash />
                            </button>
                          </div>
                        )
                  )
                }
              </div>
            ))}
          </div>
        </div>
      </Modal>
      <Modal isOpen={isFollowingModalOpen} onClose={handleCloseFollowingModal}>
        <div className="min-w-[400px] h-screen md:w-[600px] sm:h-[450px] dark:bg-gray-800 bg-gray-100 rounded-lg dark:text-white">
          <h2 className="font-[600] w-full flex items-center justify-center border-b-2 border-gray-700 p-4">Following</h2>
          <div className="max-h-[79%] overflow-y-scroll flex flex-col gap-4 p-4">
            {following?.map((oneFollow) => (
              <div key={oneFollow?.followed?.id} className=" w-full flex items-center justify-between">
                <MyAvatar
                  id={oneFollow?.followed?.id}
                  hasName
                  hasUsername
                  onClick={() => setFollowingModalOpen(false)}
                  isPost={true}
                  size="lg"
                />
                {
                  oneFollow?.followed?.id !== curUser?.id &&
                  (
                    curFollowing?.some((following) => following?.followed?.id === oneFollow?.followed?.id)
                      ? (
                        <div className="gap-4 w-full flex flex-row-reverse">
                          <button
                            className="px-5 py-2 flex items-center justify-center rounded-lg border border-gray-300 dark:border-gray-700 bg-gray-100 dark:bg-gray-900 text-sm font-[600] dark:text-white"
                            onClick={() => handleUnfollow({ followerId: curUser?.id, followedId: oneFollow?.followed?.id })}>
                            Following
                          </button>
                          <button className="px-5 py-2 flex items-center justify-center rounded-lg bg-red text-sm font-[600] text-white"
                            onClick={() => handleRemoveFollow({ followerId: oneFollow?.followed?.id, followedId: curUser?.id })}>
                            <Trash />
                          </button>
                        </div>
                      )
                      : curFollowers?.some((follower) => follower?.follower?.id === oneFollow?.followed?.id) ?
                        (
                          <div className="gap-4 w-full flex flex-row-reverse">
                            <button className="px-5 py-2 flex items-center justify-center rounded-lg bg-blue-500 text-sm font-[600] text-white"
                              onClick={() => handleFollow({ followerId: curUser?.id, followedId: oneFollow?.followed?.id })}>
                              Follow Back
                            </button>
                            <button className="px-5 py-2 flex items-center justify-center rounded-lg bg-red text-sm font-[600] text-white"
                              onClick={() => handleRemoveFollow({ followerId: oneFollow?.followed?.id, followedId: curUser?.id })}>
                              <Trash />
                            </button>
                          </div>
                        ) :
                        (
                          <div className="gap-4 w-full flex flex-row-reverse">
                            <button className="px-5 py-2 flex items-center justify-center rounded-lg bg-blue-500 text-sm font-[600] text-white"
                              onClick={() => handleFollow({ followerId: curUser?.id, followedId: oneFollow?.followed?.id })}>
                              Follow Back
                            </button>
                            <button className="px-5 py-2 flex items-center justify-center rounded-lg bg-red text-sm font-[600] text-white"
                              onClick={() => handleRemoveFollow({ followerId: oneFollow?.followed?.id, followedId: curUser?.id })}>
                              <Trash />
                            </button>
                          </div>
                        )
                  )
                }
              </div>
            ))}
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default UserProfile;
