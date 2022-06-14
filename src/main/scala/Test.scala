package test

import chisel3._
import chiseltest._
import chiseltest.internal._


class MyModule extends Module{
  val in  = IO(Input (Bool()))
  val out = IO(Output(Bool()))
  
  out := ~in
}

object Test extends App{
  
  var success = false
  val testResult = RawTester.test(new MyModule, Seq()){ dut =>
    dut.in.poke(0.U)
    dut.clock.step(1)
    dut.in.poke(1.U)
    dut.clock.step(1)
    success = true
  }
  
  println(success)
  
}
