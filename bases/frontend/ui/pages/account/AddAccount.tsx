import React from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { useToast } from "../../hooks/use-toast";
import { Button } from "../../components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "../../components/ui/card";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "../../components/ui/dialog";
import { Input } from "../../components/ui/input";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "../../components/ui/form";

// Define the schema for property data
const propertyFormSchema = z.object({
  name: z.string().min(1, "Property name is required"),
  address: z.string().min(1, "Address is required"),
  city: z.string().min(1, "City is required"),
  postalCode: z.string().min(5, "Valid postal code is required"),
  units: z.string().transform(val => parseInt(val, 10)).refine(val => !isNaN(val) && val > 0, "Must be a valid number"),
  purchasePrice: z.string().optional().transform(val => val ? parseInt(val, 10) : undefined),
  currentValue: z.string().optional().transform(val => val ? parseInt(val, 10) : undefined),
});

type PropertyFormValues = z.infer<typeof propertyFormSchema>;

export default function AddAccount(props) {
  const { toast } = useToast();

  const isLoading = props.isLoading;
  const error = "";

  // Form setup
  const form = useForm<PropertyFormValues>({
    defaultValues: {
      name: "",
      address: "",
      city: "",
      postalCode: "",
      units: "1",
      purchasePrice: "",
      currentValue: "",
    },
  });

  return (
    <Dialog open={true} onOpenChange={props.onChangeAddAccountDialogClose}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Add New Account</DialogTitle>
        </DialogHeader>
        <Form {...form}>
          <div className="space-y-4">
            <FormField
              name="name"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Name</FormLabel>
                  <FormControl>
                    <Input  placeholder="Sparkasse"
                            defaultValue={props.name} 
                            onBlur={props.onChangeName} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              name="iban"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>IBAN</FormLabel>
                  <FormControl>
                    <Input  placeholder="DE76 1256 7853 5617 2567 8953"
                            defaultValue={props.iban} 
                            onBlur={props.onChangeIban} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <div className="flex justify-end space-x-4 pt-4">
              <Button 
                type="button" 
                variant="outline"
                onClick={props.onChangeAddAccountDialogClose}
              >
                Cancel
              </Button>
              <Button disabled={false} onClick={props.submitAccount}>
                {false ? 'Saving...' : 'Save Account'}
              </Button>
            </div>
          </div>
        </Form>
      </DialogContent>
    </Dialog>
  );
}
