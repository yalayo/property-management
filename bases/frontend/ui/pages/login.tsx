import React from "react";
import { Button } from "../components/ui/button";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "../components/ui/card";
import { Input } from "../components/ui/input";
import { Label } from "../components/ui/label";
import { useAuth } from "../hooks/use-auth";
import { useForm } from "react-hook-form";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "../components/ui/form";
import { Redirect, Link } from "wouter";
import { Loader2 } from "lucide-react";

type PropertyFormValues = z.infer<typeof propertyFormSchema>;

const loginSchema = "";

type LoginFormValues = null;

export default function Login(props) {
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

  const onSubmit = (data: LoginFormValues) => {
    //loginMutation.mutate(data);
  };

  return (
    <div className="flex min-h-screen bg-gradient-to-b from-background to-background/90">
      <div className="hidden md:flex md:w-1/2 bg-primary/10 flex-col justify-center items-center p-10">
        <div className="max-w-md">
          <h1 className="text-4xl font-bold mb-6 bg-gradient-to-r from-primary to-primary/70 bg-clip-text text-transparent">
            Property Management Simplified
          </h1>
          <p className="text-muted-foreground mb-4">
            Streamline your rental property management with our comprehensive property management solution. Track payments, manage maintenance requests, and more - all in one place.
          </p>
          <ul className="space-y-2">
            <li className="flex items-center">
              <div className="rounded-full bg-primary/20 w-6 h-6 flex items-center justify-center mr-2">✓</div>
              <span>Automated payment tracking</span>
            </li>
            <li className="flex items-center">
              <div className="rounded-full bg-primary/20 w-6 h-6 flex items-center justify-center mr-2">✓</div>
              <span>Document management</span>
            </li>
            <li className="flex items-center">
              <div className="rounded-full bg-primary/20 w-6 h-6 flex items-center justify-center mr-2">✓</div>
              <span>Maintenance request handling</span>
            </li>
            <li className="flex items-center">
              <div className="rounded-full bg-primary/20 w-6 h-6 flex items-center justify-center mr-2">✓</div>
              <span>Accounting & financial reporting</span>
            </li>
          </ul>
        </div>
      </div>
      <div className="w-full md:w-1/2 flex items-center justify-center">
        <Card className="w-[350px] sm:w-[400px] shadow-lg">
          <CardHeader>
            <CardTitle className="text-2xl text-center">Login</CardTitle>
            <CardDescription className="text-center">
              Enter your credentials to access your account
            </CardDescription>
          </CardHeader>
          <CardContent>
            <Form {...form}>
              <div className="space-y-4">
                <FormField
                  control={form.control}
                  name="username"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Username</FormLabel>
                      <FormControl>
                        <Input placeholder="Enter your username" defaultValue={props.user} onBlur={props.onChangeUser} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="password"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Password</FormLabel>
                      <FormControl>
                        <Input type="password" placeholder="••••••••" defaultValue={props.password} onBlur={props.onChangePassword} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <Button
                  className="w-full"
                  onClick={props.submitLogin}
                >
                  {false ? (
                    <>
                      <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                      Logging in...
                    </>
                  ) : (
                    "Login"
                  )}
                </Button>
              </div>
            </Form>
          </CardContent>
          <CardFooter className="flex flex-col space-y-2">
            <div className="text-sm text-muted-foreground text-center">
              Don't have an account?{" "}
              <Link href="/register" className="text-primary hover:underline">
                Register
              </Link>
            </div>
          </CardFooter>
        </Card>
      </div>
    </div>
  );
}