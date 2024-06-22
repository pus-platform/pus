import React, { useState } from "react";
import Heading from "../../components/Heading/Heading";
import { LuBadgePlus } from "react-icons/lu";
import UploadFileInput from "../../components/UploadFileInput/UploadFileInput";
import Button from "../../components/common/Button/Button";
import { useNavigate } from "react-router-dom";
import Select from "../../components/common/Select/Select";
import { useUserContext } from "@/context/AuthContext";
import { useCreateStory } from "@/lib/react-query/queries/story";
import { MdOutlinePhotoLibrary, MdOutlineOndemandVideo } from "react-icons/md";
import { SlDocs } from "react-icons/sl";

const CreateStory = () => {
  const { user } = useUserContext();
  const navigate = useNavigate();
  const [formData, setFormData] = useState<any>();
  const [image, setImage] = useState<any>();

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      const file = Array.from(e.target.files)[0];
      const fileURL = URL.createObjectURL(file);
      setImage({
        url: fileURL,
        file,
        name: file.name,
      });
      setFormData(() => ({
        media: fileURL,
      }));
    }
  };

  const getFileExtension = (filename: string): string => {
    return filename?.split(".").pop()?.toLowerCase() || "";
  };

  const getFileIcon = (extension: string) => {
    if (
      extension === "png" ||
      extension === "jpeg" ||
      extension === "jpg" ||
      extension === "svg"
    ) {
      return <MdOutlinePhotoLibrary className="w-10 h-10 text-purple-500" />;
    } else if (
      extension === "mp4" ||
      extension === "avi" ||
      extension === "mkv" ||
      extension === "mov" ||
      extension === "webm" ||
      extension === "ogg"
    ) {
      return <MdOutlineOndemandVideo className="w-10 h-10 text-purple-500" />;
    } else if (extension === "pdf" || extension === "docx") {
      return <SlDocs className="w-10 h-10 text-purple-500" />;
    } else {
      return null;
    }
  };

  const { mutateAsync: addStory } = useCreateStory();
  const handleSubmit = () => {
    if (user && formData?.media.length > 0) {
      addStory({
        userId: user.id,
        image: image,
        imageUrl: formData?.media,
        type: formData?.media.match(/\.(mp4|webm)$/i) ? "video" : "image",
        header: {
          heading: user.fullname,
          subheading: user.username,
          profileImage: user.imageUrl,
        }
      });
      navigate("/");
      setImage({});
    }
  };

  return (
    <div className="flex w-full dark:text-white max-h-screen">
      <div className="flex flex-col align-center flex-grow px-4 py-6 md:px-10 lg:px-20 overflow-y-scroll">
        <Heading
          icon={<LuBadgePlus className="h-8 w-8" />}
          label="Create a Story"
        />
        <div className="self-center w-full flex flex-wrap gap-4">
          <div className="relative flex items-center gap-4  rounded-lg my-1" >
            {getFileIcon(getFileExtension(image?.name))}
            <p>{image?.name}</p>
          </div>
        </div>
        <UploadFileInput
          caption={`Add Media (${formData?.media.length} uploaded)`}
          onChange={handleFileChange}
        />
        <Select
          label="Select a Visibility"
          name="community"
          options={[
            { name: "Public", value: "public" },
            { name: "Private", value: "private" },
            ,
            { name: "Community", value: "community" },
          ]}
        />
        <div className="ml-auto my-6">
          <Button
            label="Share Story"
            size="sm"
            color="purpleFit"
            onClick={handleSubmit}
          />
        </div>
      </div>
    </div>
  );
};

export default CreateStory;
