import { createForm } from "@felte/solid";
import { For, createEffect, createResource } from "solid-js";
import toast from "solid-toast";
import { Input } from "../common/components/input";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "../common/components/table";
import { deleteTestById, getAllTest, postTest } from "../utils/requests";

export default function ApiTestPage() {
  const [data, { refetch }] = createResource(getAllTest);

  createEffect(() => {
    console.log(data());
  });

  const { form, reset } = createForm({
    onSubmit(values) {
      if (values.word) {
        postTest(values)
          .then((res) => {
            toast.success("Successfully posted your word!");
            reset();
            refetch();
          })
          .catch((error) => {
            console.log(error);
            toast.error("Something went wrong while trying to post your word.");
          })
      }
    }
  });

  return (
    <>
      <form use:form>
        <Input name="word" type="text" class="mt-4 text-center" placeholder="Add new word" />
      </form>
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead class="w-6">
              <div class="h-12 w-6"></div>
            </TableHead>
            <TableHead class="text-center">
              ID
            </TableHead>
            <TableHead class="text-center">
              Word
            </TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          <For each={data()}>
            {(test) =>
              <TestRow
                id={test.id}
                word={test.word}
                refetch={refetch}
              />
            }
          </For>
        </TableBody>
      </Table>
    </>
  );
}

function TestRow(props: {
  id: string | number,
  word: string,
  refetch: Function
}) {
  return (
    <>
      <TableRow class="text-center">
        <TableCell class="w-6">
          <div
            onClick={() => {
              deleteTestById({
                id: props.id
              })
                .then((data) => {
                  if (data.status === 204) {
                    toast.success("Successfully deleted your word!");
                    props.refetch();
                  }
                })
                .catch((error) => {
                  console.log(error);
                  toast.error("Something went wrong while trying to delete your word.");
                })
            }}
          >
            <svg
              fill="none"
              viewBox="0 0 24 24"
              stroke-width="1.5"
              stroke="currentColor"
              class="w-6 h-6"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="M14.74 9l-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 01-2.244 2.077H8.084a2.25 2.25 0 01-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 00-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 013.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 00-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 00-7.5 0"
              />
            </svg>
          </div>
        </TableCell>
        <TableCell>
          {props.id}
        </TableCell>
        <TableCell>
          {props.word}
        </TableCell>
      </TableRow>
    </>
  );
}