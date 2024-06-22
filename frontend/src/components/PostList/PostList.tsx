import PostCard from "../PostCard/PostCard";
import { Post } from "@/lib/types";

const PostList: React.FC<{ posts: Post[] }> = ({ posts }) => {
  return (
    <div className={`space-y-8`}>
      {posts?.map((post) => (
        <PostCard
          key={post?.id}
          post={{
            id: post?.id,
            user: {
              id: post?.user?.id,
              fullname: post?.user?.fullname,
              username: post?.user?.username,
              image: post?.user?.image,
            },
            caption: post?.caption,
            imageUrl: post?.imageUrl,
            likes: post?.likes,
          }}
        />
      ))}
    </div>
  );
};

export default PostList;
