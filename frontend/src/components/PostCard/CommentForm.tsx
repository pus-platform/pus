import { useAddComment } from "@/lib/react-query/queries/comment";
import {
  Card,
  Button,
  CardHeader,
  Form,
  FormField,
  FormItem,
  FormControl,
  FormMessage,
  Input,
} from '../ui'
import { useUserContext } from "@/context/AuthContext";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { CommentValidation } from "@/lib/validation";
import { Comment } from "@/lib/types";
import { SendHorizontal } from 'lucide-react';

export default function CommentForm({ postId }) {
  const { mutateAsync: addComment } = useAddComment({ post: postId })
  const { user } = useUserContext();

  const form = useForm({
    resolver: zodResolver(CommentValidation),
    defaultValues: {
      content: "",
    },
  });

  const handleComment = (value: Comment) => {
    addComment({
      ...value,
      post: postId,
    });
    form.reset();
  };

  return (
    <Card className="border-none shadow-none mt-4">
      <CardHeader className="flex flex-row justify-between w-full m-0 p-0">
        {
          user?.imageUrl ?
            <img
              src={user.imageUrl}
              alt="user"
              className="w-12 h-12 rounded-full"
            />
            :
            <img
              src="src/assets/icons/profile-placeholder.svg"
              alt="user"
              className="w-12 h-12 rounded-full"
            />
        }

        <Form {...form}>
          <form
            onSubmit={form.handleSubmit(handleComment)}
            className="flex flex-row w-full max-w-5xl"
          >
            <FormField
              control={form.control}
              name="content"
              render={({ field }) => (
                <FormItem className="w-full pr-6">
                  <FormControl
                    className="mx-3"
                  >
                    <Input
                      placeholder="Add new comment... "
                      type="text"
                      {...field}
                      autoComplete="off"
                      className="border-none dark:bg-gray-900 dark:text-white"
                    />
                  </FormControl>
                  <FormMessage className="shad-form_message" />
                </FormItem>
              )}
            />

            <Button type="submit" >
              <SendHorizontal color="#877EFF" />
            </Button>
          </form>
        </Form>
      </CardHeader>
    </Card>
  )
}