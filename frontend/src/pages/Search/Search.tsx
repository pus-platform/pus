import { useState } from "react";
import { TextInput } from "flowbite-react";
import Heading from "../../components/Heading/Heading";
import { FiSearch } from "react-icons/fi";
import MyAvatar from "../../components/common/MyAvatar/MyAvatar";
import { useGetUsers } from "@/lib/react-query/queries/user";
import PostList from "@/components/PostList/PostList";
import { useGetTrendingPosts } from "@/lib/react-query/queries/post";
import Loader from "@/components/common/Loader/Loader";
import { useNavigate } from "react-router-dom";

const Search = () => {
  const [searchQuery, setSearchQuery] = useState("");
  const [filteredSuggestions, setFilteredSuggestions] = useState<string[]>([]);
  const { data: posts, isPending: isPostLoading } = useGetTrendingPosts();
  const navigate = useNavigate();

  const { data: suggestions, isPending: isUserLoading } = useGetUsers()

  if (isPostLoading || isUserLoading)
    return (
      <Loader />
    )

  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const query = event.target.value;
    setSearchQuery(query);
    if (query.length > 0) {
      setFilteredSuggestions(
        suggestions
          .map((u) => `${u.username} ${u.fullname} ${u.id}`)
          .filter((suggestion) =>
            suggestion.toLowerCase().includes(query.toLowerCase())
          )
      );
    } else {
      setFilteredSuggestions([]);
    }
  };

  const handleSearch = () => {
    navigate('/user-profile/' + filteredSuggestions[0].split(' ').pop())
  };

  return (
    <div className="dark:text-white h-full w-full px-2 md:px-8 pb-40">
      <Heading 
        icon={<FiSearch className="h-8 w-8" />} 
        label="Search"
      />
      <div className="w-full lg:w-[500px] self-center flex items-center space-x-2">
        <TextInput
          className="bg-gray-100 dark:bg-[#101012] flex-grow"
          type="search"
          placeholder="Search"
          aria-label="Search input"
          value={searchQuery}
          onChange={handleInputChange}
          onKeyDown={(event) => {
            if (event.key === 'Enter') {
              handleSearch();
            }
          }}
        />

        <button
          onClick={handleSearch}
          className=" bg-gray-100 dark:bg-[#101012] dark:text-white rounded-[10px] px-4 py-2 border dark:border-gray-700 border-gray-200"
        >
          Search
        </button>
      </div>
      {filteredSuggestions.length > 0 && (
        <div className="absolute bg-white dark:bg-[#101012] w-full lg:w-[400px] rounded-lg mt-1 shadow-lg z-10">
          {filteredSuggestions.map((suggestion, index) => {
            return (
              <div
                key={index}
                className="px-4 py-2 hover:bg-gray-200 dark:hover:bg-gray-700 cursor-pointer"
                onClick={() => setSearchQuery(suggestion)}
              >
                <MyAvatar
                  id={parseInt(suggestion.split(" ").pop())}
                  hasName
                  hasUsername
                  isPost={true}
                  size="lg"
                />
              </div>
            )
          })}
        </div>
      )}
      <div className="w-full h-[2px] bg-gray-200 dark:bg-gray-800 my-[32px]" />
      <div className="w-full">
        <h2 className="text-[22px] my-8 font-[700]">Trending</h2>
        <div className="w-full flex justify-center items-center">
          <PostList posts={posts} />
        </div>
        {searchQuery && (
          <p className="text-gray-600 dark:text-gray-400">
            Searching for: {searchQuery}
          </p>
        )}
      </div>
    </div>
  );
};

export default Search;
