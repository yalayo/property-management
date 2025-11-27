import React from "react";
import { Button } from "../components/ui/button";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "../components/ui/card";
import { Input } from "../components/ui/input";
import { Label } from "../components/ui/label";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "../components/ui/form";
import { Redirect, Link } from "wouter";
import { Loader2 } from "lucide-react";

const registerSchema = z.object({
  email: z.string().email({ message: "Please enter a valid email" }),
  password: z.string().min(6, { message: "Password must be at least 6 characters" }),
  fullName: z.string().optional(),
});

type RegisterFormValues = z.infer<typeof registerSchema>;

export default function Register(props) {
  
  // Redirect to dashboard if already logged in
  /*if (user) {
    return <Redirect to="/dashboard" />;
  }*/

  const form = useForm<RegisterFormValues>({
    defaultValues: {
      email: "",
      password: "",
      fullName: "",
    },
  });

  const onSubmit = (data: RegisterFormValues) => {
    props.submitRegister(data);
  };

  return (
    <div className="flex min-h-screen bg-gradient-to-b from-background to-background/90">
      <div className="hidden md:flex md:w-1/2 bg-primary/10 flex-col justify-center items-center p-10">
        <div className="max-w-md">
          <h1 className="text-4xl font-bold mb-6 bg-gradient-to-r from-primary to-primary/70 bg-clip-text text-transparent">
            Join Our Property Management Platform
          </h1>
          <p className="text-muted-foreground mb-4">
            Create an account to start managing your properties efficiently. Our platform helps you streamline rental processes, track finances, and simplify property management.
          </p>
          <ul className="space-y-2">
            <li className="flex items-center">
              <div className="rounded-full bg-primary/20 w-6 h-6 flex items-center justify-center mr-2">✓</div>
              <span>One-stop solution for landlords</span>
            </li>
            <li className="flex items-center">
              <div className="rounded-full bg-primary/20 w-6 h-6 flex items-center justify-center mr-2">✓</div>
              <span>Easy tenant communication</span>
            </li>
            <li className="flex items-center">
              <div className="rounded-full bg-primary/20 w-6 h-6 flex items-center justify-center mr-2">✓</div>
              <span>Secure document storage</span>
            </li>
            <li className="flex items-center">
              <div className="rounded-full bg-primary/20 w-6 h-6 flex items-center justify-center mr-2">✓</div>
              <span>Financial tracking and reporting</span>
            </li>
          </ul>
        </div>
      </div>
      <div className="w-full md:w-1/2 flex items-center justify-center">
        <Card className="w-[350px] sm:w-[400px] shadow-lg">
          <CardHeader>
            <CardTitle className="text-2xl text-center">Create Account</CardTitle>
            <CardDescription className="text-center">
              Register to start managing your properties
            </CardDescription>
          </CardHeader>
          <CardContent>
            <Form {...form}>
              <div className="space-y-4">
                <FormField
                  control={form.control}
                  name="email"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Email</FormLabel>
                      <FormControl>
                        <Input 
                        placeholder="john.doe@example.com"
                        defaultValue={props.user}
                        onBlur={props.onChangeUser}
                      />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="fullName"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Full Name (Optional)</FormLabel>
                      <FormControl>
                        <Input 
                        placeholder="John Doe"
                        defaultValue={props.name}
                        onBlur={props.onChangeName}
                      />
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
                        <Input 
                          type="password"
                          placeholder="••••••••"
                          defaultValue={props.password}
                          onBlur={props.onChangePassword}
                      />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <Button
                  className="w-full"
                  onClick={form.handleSubmit(onSubmit)}
                >
                  {false ? (
                    <>
                      <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                      Creating account...
                    </>
                  ) : (
                    "Register"
                  )}
                </Button>
              </div>
            </Form>
          </CardContent>
          <CardFooter className="flex flex-col space-y-2">
            <div className="text-sm text-muted-foreground text-center">
              Already have an account?{" "}
              <Button className="text-primary hover:underline" onClick={props.showSignIn}>
                Login
              </Button>
            </div>
          </CardFooter>
        </Card>
      </div>
    </div>
  );
}