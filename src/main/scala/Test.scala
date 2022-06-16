package test

import chisel3._
import chisel3.util._
import chiseltest._
import chiseltest.internal._
import chiseltest.simulator.PlusArgsAnnotation

class MyModule extends Module{
  val in  = IO(Input (Bool()))
  val out = IO(Output(Bool()))
  val outpa = IO(Output(UInt(32.W)))
  
  out := ~in
  
  val pa = Module(new PlusArgReader)
  outpa := pa.io.out
  
}

class MyModuleWrap extends Module{
  val mm = Module(new MyModule)
  
  val mmin = true.B
  mm.in := mmin
  dontTouch(mm.in)
}

class PlusArgReader extends BlackBox with HasBlackBoxInline {
  val io = IO(new Bundle {
    val out = Output(UInt(32.W))
  })
  setInline("PlusArgReader.v",
  """module PlusArgReader(
    |  output [31:0] out
    |);
    |  reg [32:0] argument;
    |  assign out = argument;
    |  initial begin
    |    if (!$value$plusargs("ARGUMENT=%d", argument)) begin
    |      argument = 32'hdeadbeef;
    |    end
    |  end
    |endmodule
    |""".stripMargin)
}

object Test extends App{
  
  var success = false
  val annos = Seq(IcarusBackendAnnotation)//, PlusArgsAnnotation(s"+ARGUMENT=1234" :: Nil), WriteVcdAnnotation)
  
//   val testResult = RawTester.test(new MyModule, annos){ dut =>
//     dut.in.poke(0.U)
//     dut.clock.step(1)
//     dut.in.poke(1.U)
//     dut.clock.step(1)
//     success = true
//   }
  
  val testResult = RawTester.test(new MyModuleWrap, annos){ dut =>
    dut.mmin.poke(0.U)
    dut.clock.step(1)
    dut.mmin.poke(1.U)
    dut.clock.step(1)
    success = true
  }
  
  println(success)
  
}
