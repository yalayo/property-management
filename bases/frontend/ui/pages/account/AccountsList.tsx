import React from "react";
import { Plus, Edit, Trash2 } from "lucide-react";
import { z } from "zod";
import { useToast } from "../../hooks/use-toast";
import { Button } from "../../components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "../../components/ui/card";

// Define the schema for Account data
const propertyFormSchema = z.object({
  name: z.string().min(1, "Account name is required"),
  address: z.string().min(1, "Address is required"),
  city: z.string().min(1, "City is required"),
  postalCode: z.string().min(5, "Valid postal code is required"),
  units: z.string().transform(val => parseInt(val, 10)).refine(val => !isNaN(val) && val > 0, "Must be a valid number"),
  purchasePrice: z.string().optional().transform(val => val ? parseInt(val, 10) : undefined),
  currentValue: z.string().optional().transform(val => val ? parseInt(val, 10) : undefined),
});

type PropertyFormValues = z.infer<typeof propertyFormSchema>;

export default function AccountsList(props) {
  const { toast } = useToast();
  const isAddAccountDialogOpen = props.isAddAccountDialogOpen;

  const isLoading = props.isLoading;
  const accounts = props.accounts;
  const error = "";

  const addAccountComponent = React.Children.toArray(props.children).find(
    child => child.props['id'] === 'add-account'
  );

  if (isLoading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Accounts</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex justify-center p-4">
            <div className="animate-spin w-8 h-8 border-4 border-primary-600 border-t-transparent rounded-full" />
          </div>
        </CardContent>
      </Card>
    );
  }

  if (error) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Accounts</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="bg-red-50 p-4 rounded-md">
            <p className="text-red-800">Failed to load accounts. Please try again later.</p>
          </div>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between">
        <CardTitle>Accounts</CardTitle>

        <Button size="sm" onClick={props.onChangeAddAccountDialogOpen}>
          <Plus className="h-4 w-4 mr-2" />
          Add Account
        </Button>

        {isAddAccountDialogOpen && (
          addAccountComponent
        )}
      </CardHeader>
      <CardContent>
        {accounts && accounts.length > 0 ? (
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {accounts.map((account: any) => (
              <Card key={account.id} className="overflow-hidden">
                <div className="p-4">
                  <div className="flex justify-between items-start">
                    <div>
                      <h3 className="font-semibold text-lg">{account.name}</h3>
                      <p className="text-sm text-gray-500 mt-1">{account.iban}</p>
                    </div>
                    <div className="flex space-x-2">
                      <Button variant="ghost" size="icon" onClick={props.addNewAccount}>
                        <Plus className="h-4 w-4" />
                      </Button>
                      <Button variant="ghost" size="icon">
                        <Edit className="h-4 w-4" />
                      </Button>
                      <Button variant="ghost" size="icon">
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  </div>
                </div>
              </Card>
            ))}
          </div>
        ) : (
          <div className="text-center p-8">
            <p className="text-gray-500">No accounts found. Add your first account to get started.</p>
            <Button 
              className="mt-4"
              onClick={props.onChangeAddAccountDialogOpen}
            >
              <Plus className="h-4 w-4 mr-2" />
              Add Your First Account
            </Button>
          </div>
        )}
      </CardContent>
    </Card>
  );
}
