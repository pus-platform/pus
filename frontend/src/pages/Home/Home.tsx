import Heading from "../../components/Heading/Heading";
import PostList from "../../components/PostList/PostList";
import RenderStories from "../../components/RenderStories/RenderStories";
import { useGetRecentPosts, useGetUserPosts } from "@/lib/react-query/queries/post";
import Loader from "@/components/common/Loader/Loader";
import { useUserContext } from "@/context/AuthContext";

const Home = () => {
  const { data: posts, isPending, isRefetching, isError, isLoading, isFetching } = useGetRecentPosts();
  const { isLoading: isUserLoading, user } = useUserContext();
  const { data: userPosts, isPending: userIsPending, isRefetching: userIsRefetching, isError: userIsError, isLoading: userIsLoading, isFetching: userIsFetching } = useGetUserPosts({ userId: user?.id });
  const feed = posts?.concat(userPosts);
  if (userIsPending || userIsError || userIsRefetching || userIsLoading || userIsFetching ||
    isPending || isError || isRefetching || isLoading || isFetching || isUserLoading || !user)
    return <Loader />
  return (
    <div className="container flex flex-col space-y-12 px-[16px] align-center mx-auto w-full pt-8 min-h-screen">
      <RenderStories />
      <div className="my-[40px]">
        <Heading isFilter={true} label="Home Feed" />
      </div>
      {!!posts && posts.length > 0 ? (
        <div className="pb-8">
          <PostList posts={feed} />
        </div>
      ) : (
        <h2>No Posts Found</h2>
      )}
    </div>
  );
};

export default Home;
