import { FileInput, Label } from "flowbite-react";
import ImageVideo from "../../assets/icons/ImgVideo.svg";

interface UploadFileInputProps {
  caption?: string;
  onChange?: (event: React.ChangeEvent<HTMLInputElement>) => void;
  hidden?: boolean;
}

const UploadFileInput: React.FC<UploadFileInputProps> = ({
  caption,
  onChange,
  hidden,
}) => {
  return (
    <div className="group flex w-full flex-col justify-center">
      {caption && (
        <label className="block text-left text-lg font-semibold text-black dark:text-white py-4">
          {caption}
        </label>
      )}
      <Label
        htmlFor="dropzone-file"
        className={`flex h-64 w-full cursor-pointer flex-col items-center justify-center rounded-lg bg-white dark:bg-gray-900 dark:hover:bg-gray-800 ${hidden ? "hidden" : ""
          }`}
      >
        <div className="flex flex-col items-center justify-center pb-6 pt-5 mt-10 space-y-10">
          <img
            src={ImageVideo}
            alt=""
            width={89}
            height={69}
            className="dark:invert dark:brightness-0 transition"
          />
          <div className="flex flex-col items-center justify-center pb-6 pt-5">
            <p className="mb-2 text-sm text-gray-500 dark:text-gray-400">
              <span className="text-lg font-semibold">
                Drag photos and videos here
              </span>
            </p>
            <p className="text-xs text-gray-500 dark:text-gray-400">
              SVG, PNG, JPG or GIF (max. 800x400px)
            </p>
          </div>
          <label
            htmlFor="dropzone-file"
            className="dark:text-white bg-white shadow-md dark:bg-gray-800 px-[20px] py-[10px] rounded-lg cursor-pointer"
          >
            Select from computer
          </label>
        </div>
        <FileInput
          id="dropzone-file"
          multiple
          className="hidden"
          onChange={onChange}
        />
      </Label>
    </div>
  );
};

export default UploadFileInput;
